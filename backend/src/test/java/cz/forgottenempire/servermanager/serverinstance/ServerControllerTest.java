package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.api.model.ServerInstanceInfoDto;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessService;
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
    private static final int HEADLESS_CLIENTS_COUNT = 0;

    private final ServerInstanceService serverInstanceService;
    private final ServerProcessService serverProcessService;
    private final ServerMapper serverMapper;

    private final ServerController controller;

    public ServerControllerTest() {
        serverInstanceService = mock(ServerInstanceService.class, withSettings().stubOnly());
        serverProcessService = mock(ServerProcessService.class, withSettings().stubOnly());
        serverMapper = mock(ServerMapper.class, withSettings().stubOnly());
        controller = new ServerController(serverInstanceService, serverProcessService, serverMapper);
    }

    @Test
    void getInstanceInfo_whenServiceReturnsNull_thenNonRunningServerInstanceDtoIsReturned() {
        ServerInstanceInfoDto expectedDto = new ServerInstanceInfoDto()
                .alive(false)
                .playersOnline(0)
                .maxPlayers(0)
                .headlessClientsCount(0);
        when(serverProcessService.getServerInstanceInfo(SERVER_ID)).thenReturn(null);
        when(serverMapper.mapServerInstanceInfoToDto(any())).thenReturn(expectedDto);

        ResponseEntity<ServerInstanceInfoDto> response = controller.getServerStatus(SERVER_ID);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDto);
    }

    @Test
    void getInstanceInfo_whenServiceReturnsInstanceInfo_thenServerInstanceDtoIsReturned() {
        ServerInstanceInfo instanceInfo = new ServerInstanceInfo(STARTED_AT, PLAYERS_ONLINE, MAX_PLAYERS, VERSION, MAP,
                DESCRIPTION, HEADLESS_CLIENTS_COUNT);
        ServerInstanceInfoDto expectedDto = new ServerInstanceInfoDto()
                .alive(true)
                .startedAt(STARTED_AT.format(DateTimeFormatter.ISO_DATE_TIME))
                .playersOnline(PLAYERS_ONLINE)
                .maxPlayers(MAX_PLAYERS)
                .version(VERSION)
                .map(MAP)
                .description(DESCRIPTION)
                .headlessClientsCount(HEADLESS_CLIENTS_COUNT);
        when(serverProcessService.getServerInstanceInfo(SERVER_ID)).thenReturn(instanceInfo);
        when(serverMapper.mapServerInstanceInfoToDto(instanceInfo)).thenReturn(expectedDto);

        ResponseEntity<ServerInstanceInfoDto> response = controller.getServerStatus(SERVER_ID);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedDto);
    }
}
