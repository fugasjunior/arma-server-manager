package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@TestPropertySource(properties = "directory.mods=${java.io.tmpdir}/arma-server-manager-test/local-mod-journey-mods")
class LocalModsSyncJourneyTest extends AbstractIntegrationTest {

    @Autowired
    private LocalModService modService;

    @Autowired
    private PathsFactory pathsFactory;

    @Autowired
    private LocalModInstallerService installerService;

    @BeforeEach
    void setUp() throws IOException {
        modService.getAllMods().forEach(mod -> modService.deleteMod(mod.getId()));
        Path localModsPath = pathsFactory.getLocalModsBasePath(ServerType.ARMA3);
        FileUtils.deleteDirectory(localModsPath.toFile());
        Files.createDirectories(localModsPath);
    }

    @AfterEach
    void cleanUp() throws IOException {
        modService.getAllMods().forEach(mod -> modService.deleteMod(mod.getId()));
        FileUtils.deleteDirectory(pathsFactory.getLocalModsBasePath(ServerType.ARMA3).toFile());
    }

    @Test
    void syncLocalMods_newModDirectory_registersModAndCreatesSymlink() throws IOException {
        Path localModsPath = pathsFactory.getLocalModsBasePath(ServerType.ARMA3);
        Path modDir = localModsPath.resolve("testMod");
        Files.createDirectories(modDir);
        Files.createFile(modDir.resolve("test.pbo"));

        installerService.syncMods(ServerType.ARMA3);

        List<String> modNames = modService.getNamesForServerType(ServerType.ARMA3);
        assertThat(modNames).contains("testMod");

        List<LocalMod> mods = modService.getAllMods(ServerType.ARMA3);
        assertThat(mods).hasSize(1);
        assertThat(mods.get(0).getName()).isEqualTo("testMod");
        assertThat(mods.get(0).getServerType()).isEqualTo(ServerType.ARMA3);
        assertThat(mods.get(0).getFileSize()).isNotNull();
    }

    @Test
    void syncLocalMods_modRemovedFromFilesystem_removesDbRecord() throws IOException {
        Path localModsPath = pathsFactory.getLocalModsBasePath(ServerType.ARMA3);
        Path modDir = localModsPath.resolve("testMod");
        Files.createDirectories(modDir);
        Files.createFile(modDir.resolve("test.pbo"));

        installerService.syncMods(ServerType.ARMA3);
        List<LocalMod> modsAfterCreate = modService.getAllMods(ServerType.ARMA3);
        assertThat(modsAfterCreate).hasSize(1);
        long modId = modsAfterCreate.get(0).getId();

        FileUtils.deleteDirectory(modDir.toFile());

        installerService.syncMods(ServerType.ARMA3);

        List<LocalMod> modsAfterDelete = modService.getAllMods(ServerType.ARMA3);
        assertThat(modsAfterDelete).isEmpty();
        assertThat(modService.getMod(modId)).isEmpty();
    }

    @Test
    void syncLocalMods_modWithBikey_copiesKeyToServerKeysDir() throws IOException {
        Path localModsPath = pathsFactory.getLocalModsBasePath(ServerType.ARMA3);
        Path modDir = localModsPath.resolve("testMod");
        Files.createDirectories(modDir);
        Files.createFile(modDir.resolve("test.pbo"));
        Files.createFile(modDir.resolve("test.bikey"));

        installerService.syncMods(ServerType.ARMA3);

        List<LocalMod> mods = modService.getAllMods(ServerType.ARMA3);
        assertThat(mods).hasSize(1);
        assertThat(mods.get(0).getBiKeys()).isNotEmpty();
        assertThat(mods.get(0).getBiKeys()).contains("test.bikey");
    }
}
