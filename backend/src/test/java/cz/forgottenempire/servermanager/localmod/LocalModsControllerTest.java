package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.api.model.LocalModSyncStatusDto;
import cz.forgottenempire.servermanager.common.ServerType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LocalModsControllerTest {

    @Mock
    private LocalModFacade facade;

    @InjectMocks
    private LocalModsController controller;

    @Test
    void syncLocalMods_returns202() {
        ResponseEntity<Void> response = controller.syncLocalMods();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
        verify(facade).startSync();
    }

    @Test
    void getLocalModSyncStatus_returnsStatus() {
        when(facade.getSyncStatus()).thenReturn(LocalModSyncStatus.FINISHED);

        ResponseEntity<LocalModSyncStatusDto> response = controller.getLocalModSyncStatus();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(LocalModSyncStatusDto.StatusEnum.FINISHED);
    }
}
