package cz.forgottenempire.arma3servergui.server.installation.services.impl;

import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import cz.forgottenempire.arma3servergui.common.util.SystemUtils;
import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.server.installation.entities.ServerInstallation;
import cz.forgottenempire.arma3servergui.server.installation.repositories.ServerInstallationRepository;
import cz.forgottenempire.arma3servergui.server.installation.services.ServerInstallerService;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.services.SteamCmdService;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod.InstallationStatus;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ServerInstallerServiceImpl implements ServerInstallerService {

    @Value("${serverDir}")
    private String ARMA3_SERVER_DIR;

    private static final String LOCALHOST = "localhost";

    private final ServerInstallationRepository installationRepository;
    private final SteamCmdService steamCmdService;


    @Autowired
    public ServerInstallerServiceImpl(
            ServerInstallationRepository installationRepository,
            SteamCmdService steamCmdService
    ) {
        this.installationRepository = installationRepository;
        this.steamCmdService = steamCmdService;
    }

    @Override
    public void installServer(ServerType serverType, @Nullable String branch) {
        ServerInstallation server = installationRepository.findById(serverType)
                .orElse(new ServerInstallation(serverType));
        server.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);
        server.setErrorStatus(null);
        installationRepository.save(server);
        log.info("Starting download of server '{}'", serverType);
        steamCmdService.installOrUpdateServer(serverType, branch)
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

    private void performServerDryRun(ServerInstallation serverInstallation) throws IOException {
        int availablePort = findAvailablePort();
        Process serverProcess = startServerForDryRun(availablePort);

        int attempts = 0;
        while (attempts < 10) {
            try (SourceQueryClient sourceQueryClient = new SourceQueryClient()) {
                InetSocketAddress serverAddress = new InetSocketAddress(LOCALHOST, availablePort + 1);
                SourceServer queryServerInfo = sourceQueryClient.getServerInfo(serverAddress).get(30, TimeUnit.SECONDS);
                serverInstallation.setVersion(queryServerInfo.getGameVersion());
                break;
            } catch (Exception e) {
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

    private Process startServerForDryRun(int port) throws IOException {

        List<String> parameters = new ArrayList<>();
        parameters.add(
                Path.of(ARMA3_SERVER_DIR, "arma3server_x64").toString()); // TODO extract, enable 64bit check, ...
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.add("-port=" + port);

        return new ProcessBuilder(parameters)
                .directory(new File(ARMA3_SERVER_DIR))
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


