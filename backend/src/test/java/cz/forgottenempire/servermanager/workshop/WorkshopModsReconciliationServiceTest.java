package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkshopModsReconciliationServiceTest {

    private static final long MOD_ID = 450814997L;

    @Mock(stubOnly = true)
    private WorkshopModsService modsService;

    @Mock
    private WorkshopInstallerService installerService;

    @Mock(stubOnly = true)
    private ModMetadataService metadataService;

    @Mock(stubOnly = true)
    private PathsFactory pathsFactory;

    @Mock(stubOnly = true)
    private SteamCmdItemInfoRepository itemInfoRepository;

    @InjectMocks
    private WorkshopModsReconciliationService reconciliationService;

    @TempDir
    Path tempDir;

    @Test
    void reconcilesStaleInstalledModOutsideReadFacade() throws IOException {
        WorkshopMod mod = new WorkshopMod(MOD_ID);
        mod.setName("CBA_A3");
        mod.setServerType(ServerType.ARMA3);
        mod.setFileSize(1L);
        mod.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);

        Path manifestDirectory = tempDir.resolve("steamapps").resolve("workshop");
        Files.createDirectories(manifestDirectory);
        Files.writeString(manifestDirectory.resolve("appworkshop_107410.acf"), "\t\t\"" + MOD_ID + "\"\n");

        when(pathsFactory.getModsBasePath()).thenReturn(tempDir);
        when(itemInfoRepository.get(MOD_ID)).thenReturn(Optional.empty());

        reconciliationService.reconcile(List.of(mod));

        verify(installerService).refreshInstalledMod(mod);
    }

    @Test
    void doesNotRepairModWhileSteamCmdStillReportsAnActiveItem() throws IOException {
        WorkshopMod mod = new WorkshopMod(MOD_ID);
        mod.setName("CBA_A3");
        mod.setServerType(ServerType.ARMA3);
        mod.setFileSize(1L);
        mod.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);

        when(itemInfoRepository.get(MOD_ID)).thenReturn(Optional.of(
                new SteamCmdItemInfo(MOD_ID, SteamCmdItemInfo.SteamCmdStatus.DOWNLOADING, 0, 0, 0)));

        reconciliationService.reconcile(List.of(mod));

        verify(installerService, org.mockito.Mockito.never()).refreshInstalledMod(mod);
    }
}
