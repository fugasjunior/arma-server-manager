package cz.forgottenempire.servermanager.localmod;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalModFacadeTest {

    @Mock
    private LocalModService modService;

    @Mock
    private LocalModInstallerService installerService;

    @Mock
    private LocalModSyncStatusHolder statusHolder;

    @InjectMocks
    private LocalModFacade facade;

    @Test
    void startSync_setsInProgress_thenDelegatesToInstaller() {
        facade.startSync();

        verify(statusHolder).setStatus(LocalModSyncStatus.IN_PROGRESS);
    }

    @Test
    void getSyncStatus_returnsCurrentStatus() {
        when(statusHolder.getStatus()).thenReturn(LocalModSyncStatus.FINISHED);

        LocalModSyncStatus status = facade.getSyncStatus();

        assertThat(status).isEqualTo(LocalModSyncStatus.FINISHED);
        verify(statusHolder).getStatus();
    }

    @Test
    void setServerOnly_togglesFlag() {
        LocalMod mod = new LocalMod();
        mod.setId(1L);
        mod.setServerOnly(false);

        when(modService.requireMod(1L)).thenReturn(mod);
        when(modService.saveMod(mod)).thenReturn(mod);

        facade.setServerOnly(1L, true);

        assertThat(mod.isServerOnly()).isTrue();
        verify(modService).saveMod(mod);
    }
}
