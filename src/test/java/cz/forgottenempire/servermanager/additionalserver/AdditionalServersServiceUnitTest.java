package cz.forgottenempire.servermanager.additionalserver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class AdditionalServersServiceUnitTest {

    private final AdditionalServerRepository serverRepository;
    private final AdditionalServerInstanceInfoRepository instanceInfoRepository;
    private final ProcessFactory processFactory;
    private final AdditionalServersService serversService;

    public AdditionalServersServiceUnitTest() {
        serverRepository = mock(AdditionalServerRepository.class);
        instanceInfoRepository = mock(AdditionalServerInstanceInfoRepository.class);
        processFactory = mock(ProcessFactory.class);

        serversService = new AdditionalServersService(serverRepository, instanceInfoRepository, processFactory, "");
    }

    private static AdditionalServer createServer(Long id, String name) {
        AdditionalServer server = new AdditionalServer();
        server.setId(id);
        server.setName(name);
        server.setCommand("/bin/sh");
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

    @Test
    void whenStartServerAndServerNotRunning_thenProcessFactoryCalledAndInstanceInfoStored() throws IOException {
        AdditionalServer server = createServer(1L, "Test server");
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        Process originalProcess = mock(Process.class);
        when(originalProcess.isAlive()).thenReturn(false);
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, false, LocalDateTime.now(), originalProcess);
        when(instanceInfoRepository.getServerInstanceInfo(1L)).thenReturn(instanceInfo);
        Process newProcess = mock(Process.class);
        when(newProcess.pid()).thenReturn(1234L);
        when(processFactory.startProcessWithRedirectedOutput(any(), any(), any())).thenReturn(newProcess);

        serversService.startServer(1L);

        verify(serverRepository).findById(1L);
        verify(processFactory).startProcessWithRedirectedOutput(any(), any(), any());
        verify(instanceInfoRepository).storeServerInstanceInfo(any(), any());
    }

    @Test
    void whenStartServerAndServerAlreadyRunning_thenNoServiceIsCalled() {
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, true, LocalDateTime.now(), null);
        when(instanceInfoRepository.getServerInstanceInfo(1L)).thenReturn(instanceInfo);

        serversService.startServer(1L);

        verifyNoInteractions(serverRepository);
        verifyNoInteractions(processFactory);
        verify(instanceInfoRepository, times(0)).storeServerInstanceInfo(any(), any());
    }

    @Test
    void whenStartServerAndServerFailedToStart_thenResponseStatusExceptionIsThrown() throws IOException {
        AdditionalServer server = createServer(1L, "Test server");
        when(serverRepository.findById(1L)).thenReturn(Optional.of(server));
        Process originalProcess = mock(Process.class);
        when(originalProcess.isAlive()).thenReturn(false);
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, false, LocalDateTime.now(), originalProcess);
        when(instanceInfoRepository.getServerInstanceInfo(1L)).thenReturn(instanceInfo);
        when(processFactory.startProcessWithRedirectedOutput(any(), any(), any())).thenThrow(new IOException());

        assertThatThrownBy(() -> serversService.startServer(1L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Failed to start server 'Test server'");
    }

    @Test
    void whenStartServerAndServerDoesNotExist_thenNotFoundExceptionIsThrown() {
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, false, LocalDateTime.now(), null);
        when(instanceInfoRepository.getServerInstanceInfo(1L)).thenReturn(instanceInfo);
        when(serverRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serversService.startServer(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Additional server with ID 1 not found");
    }

    @Test
    void whenStopServerAndServerIsRunning_thenProcessIsDestroyed() {
        Process process = mock(Process.class);
        when(process.isAlive()).thenReturn(true);
        when(process.descendants()).thenReturn(Stream.empty());
        doAnswer(invocation -> {
            when(process.isAlive()).thenReturn(false);
            return null;
        }).when(process).destroy();
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, true, LocalDateTime.now(), process);
        when(instanceInfoRepository.getServerInstanceInfo(1L)).thenReturn(instanceInfo);

        serversService.stopServer(1L);

        verify(instanceInfoRepository).storeServerInstanceInfo(1L,
                new AdditionalServerInstanceInfo(1L, false, null, null));
        verify(process).destroy();
    }

    @Test
    void whenStopServerAndServerIsNotRunning_thenNoOtherActionHappens() {
        AdditionalServerInstanceInfo instanceInfo =
                new AdditionalServerInstanceInfo(1L, false, LocalDateTime.now(), null);
        when(instanceInfoRepository.getServerInstanceInfo(1L)).thenReturn(instanceInfo);

        serversService.stopServer(1L);

        verify(instanceInfoRepository, times(0)).storeServerInstanceInfo(any(), any());
    }
}