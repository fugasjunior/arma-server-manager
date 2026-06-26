package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadata;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkshopModsFacadeTest {

    private static final long MOD_ID = 450814997L;

    @Mock
    private WorkshopModsService modsService;

    @Mock
    private WorkshopInstallerService installerService;

    @Mock(stubOnly = true)
    private ModMetadataService metadataService;

    @Mock(stubOnly = true)
    private PathsFactory pathsFactory;

    @Mock(stubOnly = true)
    private SteamCmdItemInfoRepository itemInfoRepository;

    @TempDir
    Path tempDir;

    @InjectMocks
    private WorkshopModsFacade facade;

    @Test
    void addExistingFinishedModKeepsFinishedStatus() {
        WorkshopMod mod = new WorkshopMod(MOD_ID);
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        when(modsService.getMod(MOD_ID)).thenReturn(Optional.of(mod));

        List<WorkshopMod> result = facade.saveAndInstallMods(List.of(MOD_ID));

        assertThat(result.get(0).getInstallationStatus()).isEqualTo(InstallationStatus.FINISHED);
        verify(installerService).installMods(result);
    }

    @Test
    void forcedUpdateMarksExistingFinishedModInProgress() {
        WorkshopMod mod = new WorkshopMod(MOD_ID);
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        when(modsService.getMod(MOD_ID)).thenReturn(Optional.of(mod));

        List<WorkshopMod> result = facade.updateMods(List.of(MOD_ID));

        assertThat(result.get(0).getInstallationStatus()).isEqualTo(InstallationStatus.INSTALLATION_IN_PROGRESS);
        verify(installerService).updateMods(result);
    }

    @Test
    void getAllModsRepairsMissingMetadataAndFileSize() throws Exception {
        WorkshopMod mod = new WorkshopMod(MOD_ID);
        mod.setFileSize(0L);
        Path modDirectory = tempDir.resolve(String.valueOf(MOD_ID));
        Files.createDirectories(modDirectory);
        Files.writeString(modDirectory.resolve("addon.pbo"), "content");

        when(modsService.getAllMods()).thenReturn(List.of(mod));
        when(metadataService.fetchModMetadata(List.of(MOD_ID)))
                .thenReturn(Map.of(MOD_ID, new ModMetadata("CBA_A3", "107410")));
        when(pathsFactory.getModInstallationPath(MOD_ID, ServerType.ARMA3)).thenReturn(modDirectory);

        List<WorkshopMod> result = facade.getAllMods().stream().toList();

        assertThat(result).containsExactly(mod);
        assertThat(mod.getName()).isEqualTo("CBA_A3");
        assertThat(mod.getServerType()).isEqualTo(ServerType.ARMA3);
        assertThat(mod.getFileSize()).isGreaterThan(0);
        verify(modsService).saveMod(mod);
    }

    @Test
    void getAllModsRefreshesStaleInstalledModFromSteamManifest() throws Exception {
        WorkshopMod mod = new WorkshopMod(MOD_ID);
        mod.setName("CBA_A3");
        mod.setServerType(ServerType.ARMA3);
        mod.setFileSize(1L);
        mod.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);
        Path manifestDirectory = tempDir.resolve("steamapps").resolve("workshop");
        Files.createDirectories(manifestDirectory);
        Files.writeString(manifestDirectory.resolve("appworkshop_107410.acf"), "\t\t\"" + MOD_ID + "\"\n");

        when(modsService.getAllMods()).thenReturn(List.of(mod));
        when(pathsFactory.getModsBasePath()).thenReturn(tempDir);
        when(itemInfoRepository.get(MOD_ID)).thenReturn(Optional.empty());

        List<WorkshopMod> result = facade.getAllMods().stream().toList();

        assertThat(result).containsExactly(mod);
        verify(installerService).refreshInstalledMod(mod);
    }
}
