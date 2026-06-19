package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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

    @InjectMocks
    private WorkshopModsFacade facade;

    @Test
    void addExistingFinishedModKeepsFinishedStatus() {
        WorkshopMod mod = new WorkshopMod(MOD_ID);
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        when(modsService.getMod(MOD_ID)).thenReturn(Optional.of(mod));

        List<WorkshopMod> result = facade.saveAndInstallMods(List.of(MOD_ID));

        assertThat(result.get(0).getInstallationStatus()).isEqualTo(InstallationStatus.FINISHED);
        verify(installerService).installOrUpdateMods(result, false);
    }

    @Test
    void forcedUpdateMarksExistingFinishedModInProgress() {
        WorkshopMod mod = new WorkshopMod(MOD_ID);
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        when(modsService.getMod(MOD_ID)).thenReturn(Optional.of(mod));

        List<WorkshopMod> result = facade.saveAndInstallMods(List.of(MOD_ID), true);

        assertThat(result.get(0).getInstallationStatus()).isEqualTo(InstallationStatus.INSTALLATION_IN_PROGRESS);
        verify(installerService).installOrUpdateMods(result, true);
    }
}
