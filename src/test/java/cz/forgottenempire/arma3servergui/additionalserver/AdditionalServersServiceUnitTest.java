package cz.forgottenempire.arma3servergui.additionalserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class AdditionalServersServiceUnitTest {

    private final AdditionalServerRepository serverRepository;
    private final AdditionalServerInstanceInfoRepository instanceInfoRepository;
    private final AdditionalServersService serversService;

    public AdditionalServersServiceUnitTest() {
        serverRepository = mock(AdditionalServerRepository.class);
        instanceInfoRepository = mock(AdditionalServerInstanceInfoRepository.class);

        serversService = new AdditionalServersService(serverRepository, instanceInfoRepository, "");
    }

    private static AdditionalServer createServer(Long id, String name) {
        AdditionalServer server = new AdditionalServer();
        server.setId(id);
        server.setName(name);
        return server;
    }

    @Test
    void whenGetServer_thenReturnServer() {
        AdditionalServer server = createServer(1L, "Test Server");
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));

        Optional<AdditionalServer> actualServer = serversService.getServer(1L);

        assertThat(actualServer).isPresent();
        assertThat(actualServer.get()).isEqualTo(server);
    }

    @Test
    void whenGetNonExistentServer_thenReturnEmptyOptional() {
        when(serverRepository.findById(1L)).thenReturn(Optional.empty());

        Optional<AdditionalServer> actualServer = serversService.getServer(1L);

        assertThat(actualServer).isNotPresent();
    }

    @Test
    void whenGetServersAndServersExist_thenListOfServersReturned() {
        AdditionalServer server1 = createServer(1L, "Test Server 1");
        AdditionalServer server2 = createServer(2L, "Test Server 2");
        when(serverRepository.findAll()).thenReturn(List.of(server1, server2));

        List<AdditionalServer> allServers = serversService.getAllServers();

        assertThat(allServers).hasSize(2);
        assertThat(allServers).containsAll(List.of(server1, server2));
    }

    @Test
    void whenServerGetInstanceInfo_theServerInstanceInfoReturned() {
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, true, LocalDateTime.now(), null);
        when(instanceInfoRepository.getServerInstanceInfo(1L)).thenReturn(instanceInfo);

        AdditionalServerInstanceInfo actualInstanceInfo = serversService.getServerInstanceInfo(1L);

        assertThat(actualInstanceInfo).isEqualTo(instanceInfo);
    }
}