package cz.forgottenempire.arma3servergui.installation;

import cz.forgottenempire.arma3servergui.common.InstallationStatus;
import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
import cz.forgottenempire.arma3servergui.steamcmd.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.SteamCmdService;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class ServerInstallerService {

    private final ServerInstallationRepository installationRepository;
    private final SteamCmdService steamCmdService;
    private final TestRunService testRunService;

    @Autowired
    public ServerInstallerService(
            ServerInstallationRepository installationRepository,
            SteamCmdService steamCmdService,
            TestRunService testRunService) {
        this.installationRepository = installationRepository;
        this.steamCmdService = steamCmdService;
        this.testRunService = testRunService;
    }

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
                testRunService.performServerDryRun(server);
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
}


