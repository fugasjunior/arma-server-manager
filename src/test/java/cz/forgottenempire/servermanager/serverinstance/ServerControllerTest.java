package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.serverinstance.dtos.ServerInstanceInfoDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ServerControllerTest {

    private static final long SERVER_ID = 1L;
    private static final LocalDateTime STARTED_AT = LocalDateTime.now();
    private static final int PLAYERS_ONLINE = 5;
    private static final int MAX_PLAYERS = 32;
    private static final String VERSION = "1.123.456";
    private static final String MAP = "Altis";
    private static final String DESCRIPTION = "Server Description";

    private final ServerInstanceService serverInstanceService;
    private final ServerProcessService serverProcessService;

    private final ServerController controller;

    public ServerControllerTest() {
        serverInstanceService = mock(ServerInstanceService.class, withSettings().stubOnly());
        serverProcessService = mock(ServerProcessService.class, withSettings().stubOnly());
        controller = new ServerController(serverInstanceService, serverProcessService);
    }

    @Test
    void getInstanceInfo_whenServiceReturnsNull_thenNonRunningServerInstanceDtoIsReturned() {
        when(serverProcessService.getServerInstanceInfo(SERVER_ID)).thenReturn(null);

        ResponseEntity<ServerInstanceInfoDto> response = controller.getInstanceInfo(SERVER_ID);

        ServerInstanceInfoDto expectedDto = new ServerInstanceInfoDto(false, null, 0, 0, null, null, null);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDto);
    }

    @Test
    void getInstanceInfo_whenServiceReturnsInstanceInfo_thenServerInstanceDtoIsReturned() {
        ServerInstanceInfo instanceInfo = new ServerInstanceInfo(STARTED_AT, PLAYERS_ONLINE, MAX_PLAYERS, VERSION, MAP, DESCRIPTION);
        when(serverProcessService.getServerInstanceInfo(SERVER_ID)).thenReturn(instanceInfo);

        ResponseEntity<ServerInstanceInfoDto> response = controller.getInstanceInfo(SERVER_ID);

        ServerInstanceInfoDto expectedDto = new ServerInstanceInfoDto(true, STARTED_AT.format(DateTimeFormatter.ISO_DATE_TIME),
                PLAYERS_ONLINE, MAX_PLAYERS, VERSION, MAP, DESCRIPTION);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDto);
    }
}
