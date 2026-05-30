package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.api.model.DayZServerDto;
import cz.forgottenempire.servermanager.api.model.ServerDto;
import cz.forgottenempire.servermanager.api.model.ServerInstanceInfoDto;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.DayZServer;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServerControllerTest {

    private static final long SERVER_ID = 1L;
    private static final long NEW_SERVER_ID = 2L;
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
    private final Arma3InstanceDataCopier copier;

    private final ServerController controller;

    public ServerControllerTest() {
        serverInstanceService = mock(ServerInstanceService.class, withSettings().stubOnly());
        serverProcessService = mock(ServerProcessService.class, withSettings().stubOnly());
        serverMapper = mock(ServerMapper.class, withSettings().stubOnly());
        copier = mock(Arma3InstanceDataCopier.class);
        controller = new ServerController(serverInstanceService, serverProcessService, serverMapper,
                mock(PathsFactory.class, withSettings().stubOnly()),
                mock(ServerSecretsMasker.class, withSettings().stubOnly()),
                mock(ConfigOverrideService.class, withSettings().stubOnly()),
                copier);
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

    @Test
    void duplicateServer_whenArma3Source_returnsCreatedAndInvokesCopier() {
        Arma3Server source = new Arma3Server();
        source.setId(SERVER_ID);
        source.setName("My Server");

        Arma3Server saved = new Arma3Server();
        saved.setId(NEW_SERVER_ID);

        ServerDto sourceDto = new cz.forgottenempire.servermanager.api.model.Arma3ServerDto();

        when(serverInstanceService.getServer(SERVER_ID)).thenReturn(Optional.of(source));
        when(serverMapper.mapServerToDto(source)).thenReturn(sourceDto);
        when(serverMapper.mapServerDtoToEntity(any())).thenReturn(new Arma3Server());
        when(serverInstanceService.createServer(any(), any())).thenAnswer(inv -> {
            Server clone = inv.getArgument(0);
            assertThat(clone.getId()).isNull();
            assertThat(clone.getName()).isEqualTo("My Server (copy)");
            return saved;
        });
        when(serverMapper.mapServerToDto(saved)).thenReturn(new cz.forgottenempire.servermanager.api.model.Arma3ServerDto());

        ResponseEntity<ServerDto> response = controller.duplicateServer(SERVER_ID);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        verify(copier).copyInstanceData(SERVER_ID, NEW_SERVER_ID);
    }

    @Test
    void duplicateServer_whenNonArma3Source_doesNotInvokeCopier() {
        DayZServer source = new DayZServer();
        source.setId(SERVER_ID);
        source.setName("DayZ Server");

        DayZServer saved = new DayZServer();
        saved.setId(NEW_SERVER_ID);

        DayZServerDto sourceDto = new DayZServerDto();

        when(serverInstanceService.getServer(SERVER_ID)).thenReturn(Optional.of(source));
        when(serverMapper.mapServerToDto(source)).thenReturn(sourceDto);
        when(serverMapper.mapServerDtoToEntity(any())).thenReturn(new DayZServer());
        when(serverInstanceService.createServer(any(), any())).thenReturn(saved);
        when(serverMapper.mapServerToDto(saved)).thenReturn(new DayZServerDto());

        controller.duplicateServer(SERVER_ID);

        verify(copier, never()).copyInstanceData(anyLong(), anyLong());
    }

    @Test
    void duplicateServer_whenServerNotFound_throwsNotFoundException() {
        when(serverInstanceService.getServer(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.duplicateServer(999L))
                .isInstanceOf(NotFoundException.class);
    }
}
