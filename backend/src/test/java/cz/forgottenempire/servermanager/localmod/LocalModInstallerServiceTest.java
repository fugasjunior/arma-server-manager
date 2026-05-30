package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalModInstallerServiceTest {

    @Mock
    private PathsFactory pathsFactory;

    @Mock
    private LocalModService modService;

    @Mock
    private ServerInstallationService installationService;

    @InjectMocks
    private LocalModInstallerService installerService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        lenient().when(installationService.getInstalledRelatedServerTypes(ServerType.ARMA3)).thenReturn(List.of(ServerType.ARMA3));
        lenient().when(installationService.getInstalledRelatedServerTypes(ServerType.DAYZ)).thenReturn(List.of());
        lenient().when(installationService.getInstalledRelatedServerTypes(ServerType.DAYZ_EXP)).thenReturn(List.of());
    }

    @Test
    void syncMods_newDirFound_createsDbEntryAndInstalls() throws IOException {
        ServerType serverType = ServerType.ARMA3;

        Path baseModsPath = tempDir.resolve("local/ARMA3");
        Files.createDirectories(baseModsPath);
        Path modDir = baseModsPath.resolve("testMod");
        Files.createDirectories(modDir);
        Path linkPath = tempDir.resolve("servers/ARMA3/testMod");
        Files.createDirectories(linkPath.getParent());

        when(modService.getNamesForServerType(serverType)).thenReturn(List.of());
        when(modService.getAllMods(serverType)).thenReturn(List.of());
        when(pathsFactory.getLocalModsBasePath(serverType)).thenReturn(baseModsPath);
        when(pathsFactory.getLocalModPath("testMod", serverType)).thenReturn(modDir);
        when(pathsFactory.getLocalModLinkPath("testMod", serverType)).thenReturn(linkPath);
        when(modService.saveMod(any(LocalMod.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> installerService.syncMods(serverType)).doesNotThrowAnyException();

        verify(modService).saveMod(any(LocalMod.class));
    }

    @Test
    void installBiKeys_copiesBikeyToServerKeysDir() throws IOException {
        ServerType serverType = ServerType.ARMA3;

        Path baseModsPath = tempDir.resolve("local/ARMA3");
        Files.createDirectories(baseModsPath);
        Path modDir = baseModsPath.resolve("testMod");
        Files.createDirectories(modDir);
        Files.createFile(modDir.resolve("mod.bikey"));

        Path serverKeysDir = tempDir.resolve("servers/ARMA3/keys");
        Files.createDirectories(serverKeysDir);
        Path keyPath = serverKeysDir.resolve("mod.bikey");
        Path linkPath = tempDir.resolve("servers/ARMA3/testMod");

        when(modService.getNamesForServerType(serverType)).thenReturn(List.of());
        when(modService.getAllMods(serverType)).thenReturn(List.of());
        when(pathsFactory.getLocalModsBasePath(serverType)).thenReturn(baseModsPath);
        when(pathsFactory.getLocalModPath("testMod", serverType)).thenReturn(modDir);
        when(pathsFactory.getLocalModLinkPath("testMod", serverType)).thenReturn(linkPath);
        when(pathsFactory.getServerKeyPath("mod.bikey", serverType)).thenReturn(keyPath);
        when(modService.saveMod(any(LocalMod.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> installerService.syncMods(serverType)).doesNotThrowAnyException();

        assertThat(keyPath).exists();
    }

    @Test
    void createSymlink_skipsIfAlreadyExists() throws IOException {
        ServerType serverType = ServerType.ARMA3;

        Path baseModsPath = tempDir.resolve("local/ARMA3");
        Files.createDirectories(baseModsPath);
        Path modDir = baseModsPath.resolve("testMod");
        Files.createDirectories(modDir);
        Path linkPath = tempDir.resolve("servers/ARMA3/testMod");
        Files.createDirectories(linkPath.getParent());
        Files.createSymbolicLink(linkPath, modDir);

        LocalMod existingMod = new LocalMod();
        existingMod.setName("testMod");
        existingMod.setServerType(serverType);

        when(modService.getNamesForServerType(serverType)).thenReturn(List.of("testMod"));
        when(modService.getAllMods(serverType)).thenReturn(List.of(existingMod));
        when(pathsFactory.getLocalModsBasePath(serverType)).thenReturn(baseModsPath);
        when(pathsFactory.getLocalModPath("testMod", serverType)).thenReturn(modDir);
        when(pathsFactory.getLocalModLinkPath("testMod", serverType)).thenReturn(linkPath);
        when(modService.saveMod(any(LocalMod.class))).thenAnswer(inv -> inv.getArgument(0));

        assertThatCode(() -> installerService.syncMods(serverType)).doesNotThrowAnyException();

        assertThat(linkPath).isSymbolicLink();
    }

    @Test
    void syncMods_modRemovedFromFilesystem_deletesDbEntry() throws IOException {
        ServerType serverType = ServerType.ARMA3;

        Path baseModsPath = tempDir.resolve("local/ARMA3");
        Files.createDirectories(baseModsPath);

        LocalMod existingMod = new LocalMod();
        existingMod.setId(1L);
        existingMod.setName("removedMod");
        existingMod.setServerType(serverType);

        when(modService.getNamesForServerType(serverType)).thenReturn(List.of("removedMod"));
        when(modService.getAllMods(serverType)).thenReturn(List.of(existingMod));
        when(pathsFactory.getLocalModsBasePath(serverType)).thenReturn(baseModsPath);
        when(pathsFactory.getLocalModLinkPath("removedMod", serverType))
                .thenReturn(tempDir.resolve("servers/ARMA3/removedMod"));

        assertThatCode(() -> installerService.syncMods(serverType)).doesNotThrowAnyException();

        verify(modService).deleteMod(1L);
    }
}
