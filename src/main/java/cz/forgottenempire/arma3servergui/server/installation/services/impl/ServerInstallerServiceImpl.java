package cz.forgottenempire.arma3servergui.server.installation.services.impl;

import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import cz.forgottenempire.arma3servergui.common.services.PathsFactory;
import cz.forgottenempire.arma3servergui.common.util.SystemUtils;
import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.server.installation.entities.ServerInstallation;
import cz.forgottenempire.arma3servergui.server.installation.repositories.ServerInstallationRepository;
import cz.forgottenempire.arma3servergui.server.installation.services.ServerInstallerService;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.services.SteamCmdService;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod.InstallationStatus;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServerInstallerServiceImpl implements ServerInstallerService {

    private static final String LOCALHOST = "localhost";

    private final ServerInstallationRepository installationRepository;
    private final SteamCmdService steamCmdService;
    private final PathsFactory pathsFactory;


    @Autowired
    public ServerInstallerServiceImpl(
            ServerInstallationRepository installationRepository,
            SteamCmdService steamCmdService,
            PathsFactory pathsFactory) {
        this.installationRepository = installationRepository;
        this.steamCmdService = steamCmdService;
        this.pathsFactory = pathsFactory;
    }

    @Override
    public void installServer(ServerType serverType) {
        ServerInstallation server = installationRepository.findById(serverType)
                .orElse(new ServerInstallation(serverType));
        server.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);
        server.setErrorStatus(null);
        installationRepository.save(server);
        log.info("Starting download of server '{}'", serverType);
        steamCmdService.installOrUpdateServer(serverType)
                .thenAcceptAsync(steamCmdJob -> handleInstallation(steamCmdJob, server));
    }

    private void handleInstallation(SteamCmdJob steamCmdJob, ServerInstallation server) {
        if (steamCmdJob.getErrorStatus() != null) {
            log.error("Download of server '{}' failed, reason: {}",
                    server.getType(), steamCmdJob.getErrorStatus());
            server.setInstallationStatus(InstallationStatus.ERROR);
            server.setErrorStatus(steamCmdJob.getErrorStatus());
        } else {
            try {
                log.info("Server '{}' successfully downloaded, verifying...", server.getType());
                performServerDryRun(server);
                log.info("Server '{}' successfully installed", server.getType());
                server.setLastUpdatedAt(LocalDateTime.now());
                server.setInstallationStatus(InstallationStatus.FINISHED);
            } catch (Exception e) {
                log.error("Server '{}' failed to start after installation", server.getType(), e);
                server.setInstallationStatus(InstallationStatus.ERROR);
                server.setErrorStatus(ErrorStatus.GENERIC);
            }
        }
        installationRepository.save(server);
    }

    private synchronized void performServerDryRun(ServerInstallation serverInstallation) throws IOException {

        int availablePort = findAvailablePort();

        ServerType type = serverInstallation.getType();
        createTestConfigFile(type, availablePort);

        Process serverProcess = startServerForDryRun(type, availablePort);

        int attempts = 0;
        while (attempts < 10) {
            try (SourceQueryClient sourceQueryClient = new SourceQueryClient()) {
                InetSocketAddress serverAddress = new InetSocketAddress(LOCALHOST, availablePort + 1);
                SourceServer queryServerInfo = sourceQueryClient.getServerInfo(serverAddress).get(30, TimeUnit.SECONDS);
                serverInstallation.setVersion(queryServerInfo.getGameVersion());
                break;
            } catch (Exception e) {
                if (!serverProcess.isAlive()) {
                    log.error("Server crashed before it could be queried");
                    throw new RuntimeException(e);
                }
                // no matter of the timeout set for CompletableFuture#get(), the several first query requests fail
                // automatically after 5 seconds. That's why this ugly loop for retrying is needed.
                attempts++;
                if (attempts == 10) {
                    serverProcess.destroy();
                    throw new RuntimeException(e);
                }
            }
        }
        serverProcess.destroy();
    }

    private File createTestConfigFile(ServerType type, int availablePort) throws IOException {
        File testCfgFile = Path.of(pathsFactory.getServerPath(type).toString(), "TEST_CONFIG.cfg").toFile();
        if (testCfgFile.isFile()) {
            testCfgFile.delete();
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(testCfgFile));
        writer.write("hostName=\"TEST SERVER\"\n");
        if (type == ServerType.DAYZ || type == ServerType.DAYZ_EXP) {
            writer.write("instanceId=1\n");
            writer.write("steamQueryPort=" + availablePort + "\n");
        }
        writer.close();
        return testCfgFile;
    }

    private Process startServerForDryRun(ServerType type, int port) throws IOException {
        List<String> parameters = new ArrayList<>();
        parameters.add(pathsFactory.getServerExecutable(type).toString());
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.add("-config=TEST_CONFIG.cfg");
        parameters.add("-port=" + port);

        log.info("Starting server '{}' for dry run with parameters: {}", type, parameters);

        return new ProcessBuilder(parameters)
                .directory(pathsFactory.getServerPath(type).toFile())
                .start();
    }

    private int findAvailablePort() {
        int port = 3000;
        while (!SystemUtils.isPortAvailable(port) || !SystemUtils.isPortAvailable(port + 1)) {
            port += 2;
        }
        return port;
    }
}


