package cz.forgottenempire.servermanager.localmod;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import cz.forgottenempire.servermanager.api.model.ModFlagsDto;

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
    void setFlags_updatesAllFlags() {
        LocalMod mod = new LocalMod();
        mod.setId(1L);
        mod.setLoadOnClient(true);
        mod.setLoadOnServer(true);
        mod.setLoadOnHeadlessClient(true);

        ModFlagsDto flags = new ModFlagsDto(false, true, false);

        when(modService.requireMod(1L)).thenReturn(mod);
        when(modService.saveMod(mod)).thenReturn(mod);

        facade.setFlags(1L, flags);

        assertThat(mod.isLoadOnClient()).isFalse();
        assertThat(mod.isLoadOnServer()).isTrue();
        assertThat(mod.isLoadOnHeadlessClient()).isFalse();
        verify(modService).saveMod(mod);
    }
}
