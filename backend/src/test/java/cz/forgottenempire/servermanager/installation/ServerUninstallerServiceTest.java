package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;
import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ServerUninstallerServiceTest {

    @TempDir
    Path tempDir;

    ServerInstallationRepository installationRepository;
    ServerRepository serverRepository;
    ServerProcessService processService;
    PathsFactory pathsFactory;
    ServerUninstallerService service;

    @BeforeEach
    void setUp() {
        installationRepository = mock(ServerInstallationRepository.class);
        serverRepository = mock(ServerRepository.class);
        processService = mock(ServerProcessService.class);
        pathsFactory = mock(PathsFactory.class);
        service = new ServerUninstallerService(installationRepository, serverRepository, processService, pathsFactory);
    }

    @Test
    void uninstallServer_happyPath_deletesFilesAndResetsStatus() throws IOException {
        Path serverDir = tempDir.resolve("ARMA3");
        Files.createDirectories(serverDir);
        Files.createFile(serverDir.resolve("arma3server_x64"));

        ServerInstallation installation = installedInstallation(ServerType.ARMA3);
        when(pathsFactory.getServerPath(ServerType.ARMA3)).thenReturn(serverDir);
        when(serverRepository.findAll()).thenReturn(List.of());

        service.uninstallServer(installation);

        assertThat(serverDir).doesNotExist();
        assertThat(installation.getInstallationStatus()).isNull();
        assertThat(installation.getErrorStatus()).isNull();
        assertThat(installation.getVersion()).isNull();
        assertThat(installation.getLastUpdatedAt()).isNull();
        verify(installationRepository).save(installation);
    }

    @Test
    void uninstallServer_directoryMissing_stillResetsStatus() {
        Path serverDir = tempDir.resolve("DAYZ");

        ServerInstallation installation = installedInstallation(ServerType.DAYZ);
        when(pathsFactory.getServerPath(ServerType.DAYZ)).thenReturn(serverDir);
        when(serverRepository.findAll()).thenReturn(List.of());

        service.uninstallServer(installation);

        assertThat(installation.getInstallationStatus()).isNull();
        verify(installationRepository).save(installation);
    }

    @Test
    void uninstallServer_installationInProgress_throwsConflict() {
        ServerInstallation installation = new ServerInstallation();
        installation.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);

        assertThatThrownBy(() -> service.uninstallServer(installation))
                .isInstanceOf(InstallationInProgressException.class);

        verifyNoInteractions(pathsFactory, installationRepository);
    }

    @Test
    void uninstallServer_serverRunning_throwsConflict() {
        Server runningServer = mock(Server.class);
        when(runningServer.getType()).thenReturn(ServerType.ARMA3);
        when(serverRepository.findAll()).thenReturn(List.of(runningServer));
        when(processService.isServerInstanceRunning(runningServer)).thenReturn(true);

        ServerInstallation installation = installedInstallation(ServerType.ARMA3);

        assertThatThrownBy(() -> service.uninstallServer(installation))
                .isInstanceOf(CustomUserErrorException.class);

        verifyNoInteractions(pathsFactory, installationRepository);
    }

    @Test
    void uninstallServer_otherTypeRunning_doesNotBlock() throws IOException {
        Path serverDir = tempDir.resolve("ARMA3");
        Files.createDirectories(serverDir);

        Server dayzServer = mock(Server.class);
        when(dayzServer.getType()).thenReturn(ServerType.DAYZ);
        when(serverRepository.findAll()).thenReturn(List.of(dayzServer));
        when(processService.isServerInstanceRunning(dayzServer)).thenReturn(true);

        ServerInstallation installation = installedInstallation(ServerType.ARMA3);
        when(pathsFactory.getServerPath(ServerType.ARMA3)).thenReturn(serverDir);

        service.uninstallServer(installation);

        assertThat(installation.getInstallationStatus()).isNull();
        verify(installationRepository).save(installation);
    }

    private ServerInstallation installedInstallation(ServerType type) {
        ServerInstallation installation = new ServerInstallation();
        installation.setType(type);
        installation.setInstallationStatus(InstallationStatus.FINISHED);
        installation.setVersion("2.18.0");
        installation.setLastUpdatedAt(LocalDateTime.now());
        return installation;
    }
}
