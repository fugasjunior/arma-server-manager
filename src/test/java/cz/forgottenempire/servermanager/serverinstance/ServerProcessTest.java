package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServerProcessTest {

    private static final long SERVER_ID = 1L;

    private final ServerProcess serverProcess = new ServerProcess(SERVER_ID);

    @Test
    void getServerId_whenCalled_thenReturnsServerId() {
        assertThat(serverProcess.getServerId()).isEqualTo(SERVER_ID);
    }

    @Test
    void start_whenServerIsStarted_thenNewProcessIsCreatedAndReturned() throws IOException {
        ServerRepository serverRepository = mock(ServerRepository.class);
        Server server = mock(Server.class);
        List<String> parameters = List.of("-test=parameter");
        when(server.getLaunchParameters()).thenReturn(parameters);
        when(server.getType()).thenReturn(ServerType.ARMA3);
        ServerLog serverLog = mock(ServerLog.class);
        File logFile = mock(File.class);
        when(serverLog.getFile()).thenReturn(logFile);
        when(server.getLog()).thenReturn(serverLog);
        when(serverRepository.findById(SERVER_ID)).thenReturn(Optional.of(server));
        ServerProcessCreator processCreator = mock(ServerProcessCreator.class);
        Process expectedProcess = mock(Process.class);
        when(processCreator.startProcessWithRedirectedOutput(any(), any(), any())).thenReturn(expectedProcess);
        serverProcess.setServerProcessCreator(processCreator);
        serverProcess.setServerRepository(serverRepository);
        PathsFactory pathsFactory = mock(PathsFactory.class);
        File executable = mock(File.class);
        when(pathsFactory.getServerExecutableWithFallback(ServerType.ARMA3)).thenReturn(executable);
        serverProcess.setPathsFactory(pathsFactory);

        Process actualProcess = serverProcess.start();

        assertThat(actualProcess).isEqualTo(actualProcess);
        verify(processCreator).startProcessWithRedirectedOutput(executable, parameters, logFile);
    }
}