package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;
import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Path;

@Service
@Slf4j
class ServerUninstallerService {

    private final ServerInstallationRepository installationRepository;
    private final ServerRepository serverRepository;
    private final ServerProcessService processService;
    private final PathsFactory pathsFactory;

    @Autowired
    public ServerUninstallerService(
            ServerInstallationRepository installationRepository,
            ServerRepository serverRepository,
            ServerProcessService processService,
            PathsFactory pathsFactory
    ) {
        this.installationRepository = installationRepository;
        this.serverRepository = serverRepository;
        this.processService = processService;
        this.pathsFactory = pathsFactory;
    }

    public void uninstallServer(ServerInstallation installation) {
        ServerType type = installation.getType();

        if (installation.getInstallationStatus() == InstallationStatus.INSTALLATION_IN_PROGRESS) {
            throw new InstallationInProgressException(type);
        }

        boolean anyRunning = serverRepository.findAll().stream()
                .filter(s -> s.getType() == type)
                .anyMatch(processService::isServerInstanceRunning);
        if (anyRunning) {
            throw new CustomUserErrorException(
                    "Cannot uninstall server '" + type + "' while it is running", HttpStatus.CONFLICT);
        }

        Path serverPath = pathsFactory.getServerPath(type);
        try {
            FileSystemUtils.deleteRecursively(serverPath);
            log.info("Deleted server directory for '{}'", type);
        } catch (IOException e) {
            log.error("Failed to delete server directory for '{}'", type, e);
            throw new RuntimeException("Failed to delete server files for " + type, e);
        }

        installation.setInstallationStatus(null);
        installation.setErrorStatus(null);
        installation.setVersion(null);
        installation.setLastUpdatedAt(null);
        installationRepository.save(installation);
        log.info("Uninstalled server '{}'", type);
    }
}
