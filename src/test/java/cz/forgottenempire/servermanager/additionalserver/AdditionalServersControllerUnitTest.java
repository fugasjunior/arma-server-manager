package cz.forgottenempire.servermanager.additionalserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AdditionalServersControllerUnitTest {

    private AdditionalServersService serversService;
    private AdditionalServersController controller;

    @BeforeEach
    void setUp() {
        serversService = Mockito.mock(AdditionalServersService.class);

        controller = new AdditionalServersController(serversService);
    }

    @Test
    void whenGetServersAndServerExists_thenReturnServersDto() {
        AdditionalServer server1 = createServer(1L, "Test server 1");
        AdditionalServer server2 = createServer(2L, "Test server 2");
        when(serversService.getAllServers()).thenReturn(List.of(server1, server2));

        ResponseEntity<AdditionalServersDto> response = controller.getAdditionalServers();

        AdditionalServerDto expectedServer1 = createServerDto(1L, "Test server 1");
        AdditionalServerDto expectedServer2 = createServerDto(2L, "Test server 2");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getServers()).hasSize(2);
        assertThat(response.getBody().getServers()).contains(expectedServer1, expectedServer2);
    }

    @Test
    void whenGetServersAndNoServerExists_thenReturnEmptyServersDto() {
        when(serversService.getAllServers()).thenReturn(Collections.emptyList());

        ResponseEntity<AdditionalServersDto> additionalServers = controller.getAdditionalServers();

        assertThat(additionalServers.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(additionalServers.getBody()).isNotNull();
        assertThat(additionalServers.getBody().getServers()).isEmpty();
    }

    @Test
    void whenGetServerAndServerExists_thenReturnServerDto() {
        AdditionalServer server = createServer(1L, "Test server");
        when(serversService.getServer(1L)).thenReturn(Optional.of(server));

        ResponseEntity<AdditionalServerDto> additionalServer = controller.getAdditionalServer(1L);

        AdditionalServerDto expectedServer = createServerDto(1L, "Test server");
        assertThat(additionalServer.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(additionalServer.getBody()).isNotNull();
        assertThat(additionalServer.getBody()).isEqualTo(expectedServer);
    }

    @Test
    void whenGetServerAndServerDoesntExist_thenNotFoundExceptionIsThrown() {
        when(serversService.getServer(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.getAdditionalServer(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Additional server ID 1 not found");
    }

    @Test
    void whenStartServer_thenServiceStartServerMethodIsCalledAndOkResponseIsReturned() {
        ResponseEntity<?> response = controller.startServer(1L);

        verify(serversService).startServer(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void whenStopServer_thenServiceStopServerMethodIsCalledAndOkResponseIsReturned() {
        ResponseEntity<?> response = controller.stopServer(1L);

        verify(serversService).stopServer(1L);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    private AdditionalServer createServer(Long id, String name) {
        AdditionalServer server = new AdditionalServer();
        server.setId(id);
        server.setName(name);
        server.setCommand("/bin/sh dostuff");
        server.setServerDir("/home/server/additionalserver/" + name);
        server.setImageUrl("https://example.com/image.jpg");
        return server;
    }

    private AdditionalServerDto createServerDto(Long id, String name) {
        AdditionalServerDto server = new AdditionalServerDto();
        server.setId(id);
        server.setName(name);
        server.setImageUrl("https://example.com/image.jpg");
        return server;
    }


}