package cz.forgottenempire.arma3servergui.server.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import cz.forgottenempire.arma3servergui.server.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.server.dtos.ServersDto;
import cz.forgottenempire.arma3servergui.server.entities.Server;
import cz.forgottenempire.arma3servergui.server.services.ArmaServerService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

class ServerControllerUnitTest {

    ArmaServerService serverService;

    ServerController serverController;

    @BeforeEach
    void setUp() {
        serverService = mock(ArmaServerService.class);

        serverController = new ServerController();
        serverController.setServerService(serverService);
    }

    @Test
    void whenGetAllServersAndNoServersPresent_thenReturnEmptyList() {
        when(serverService.getAllServers()).thenReturn(new ArrayList<>());

        ResponseEntity<ServersDto> serversResponseEntity = serverController.getAllServers();

        assertThat(serversResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(serversResponseEntity.getBody()).isNotNull();
        assertThat(serversResponseEntity.getBody().getServers()).isEmpty();
    }

    @Test
    void whenGetAllServersAndServersArePresent_thenReturnServers() {
        Server server1 = new Server();
        server1.setId(1L);
        server1.setName("Arma 3 Mike Force server");
        server1.setPort(2302);
        Server server2 = new Server();
        server2.setId(2L);
        server2.setName("Arma 3 Epoch server");
        server2.setPort(2402);
        when(serverService.getAllServers()).thenReturn(List.of(server1, server2));

        ResponseEntity<ServersDto> serversResponseEntity = serverController.getAllServers();

        assertThat(serversResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(serversResponseEntity.getBody()).isNotNull();
        List<ServerDto> servers = serversResponseEntity.getBody().getServers();
        assertThat(servers.size()).isEqualTo(2);
        ServerDto serverDto1 = servers.get(0);
        assertThat(serverDto1.getId()).isEqualTo(1);
        assertThat(serverDto1.getName()).isEqualTo("Arma 3 Mike Force server");
        assertThat(serverDto1.getPort()).isEqualTo(2302);
        ServerDto serverDto2 = servers.get(1);
        assertThat(serverDto2.getId()).isEqualTo(2);
        assertThat(serverDto2.getName()).isEqualTo("Arma 3 Epoch server");
        assertThat(serverDto2.getPort()).isEqualTo(2402);
    }

    @Test
    void whenGetServer_thenServerDtoIsReturned() {
        Server server = new Server();
        server.setId(1L);
        server.setName("Arma 3 Mike Force server");
        server.setPort(2302);
        when(serverService.getServer(1L)).thenReturn(Optional.of(server));

        ResponseEntity<ServerDto> responseEntity = serverController.getServer(1L);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        ServerDto serverDto = responseEntity.getBody();
        assertThat(serverDto).isNotNull();
        assertThat(serverDto.getId()).isEqualTo(1L);
        assertThat(serverDto.getPort()).isEqualTo(2302);
    }

    @Test
    void whenGetServerDoesNotExist_thenNotFoundExceptionThrown() {
        when(serverService.getServer(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serverController.getServer(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND")
                .hasMessageContaining("Server ID 1 doesn't exist");
    }

    @Test
    void whenCreateServer_thenServerDtoReturned() {
        ServerDto requestDto = new ServerDto();
        requestDto.setName("Arma 3 server");
        requestDto.setPort(2302);
        Server createdServer = new Server();
        createdServer.setId(1L);
        createdServer.setName("Arma 3 server");
        createdServer.setPort(2302);
        when(serverService.createServer(any(Server.class))).thenReturn(createdServer);

        ResponseEntity<ServerDto> responseEntity = serverController.createServer(requestDto);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ServerDto serverDto = responseEntity.getBody();
        assertThat(serverDto).isNotNull();
        assertThat(serverDto.getId()).isEqualTo(1L);
        assertThat(serverDto.getName()).isEqualTo("Arma 3 server");
        assertThat(serverDto.getPort()).isEqualTo(2302);
    }

    @Test
    void whenUpdateServerThatDoesntExist_thenNotFoundExceptionThrown() {
        when(serverService.getServer(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serverController.updateServer(1L, new ServerDto()))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("404 NOT_FOUND")
                .hasMessageContaining("Server ID 1 doesn't exist");
    }

    @Test
    void whenDeleteServer_thenNoContentResponseReturned() {
        when(serverService.getServer(anyLong())).thenReturn(Optional.of(new Server()));

        ResponseEntity<?> responseEntity = serverController.deleteServer(1L);

        assertThat(responseEntity.getBody()).isNull();
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}