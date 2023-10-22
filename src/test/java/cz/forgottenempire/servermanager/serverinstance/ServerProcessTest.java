package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import org.junit.jupiter.api.BeforeEach;
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

    private ServerProcess serverProcess;
    private Server server;
    private ServerRepository serverRepository;
    private File logFile;
    private ServerLog serverLog;
    private PathsFactory pathsFactory;
    private File executable;
    private ServerProcessCreator processCreator;
    private Process process;

    @BeforeEach
    void setUp() throws IOException {
        server = mock(Server.class);
        when(server.getType()).thenReturn(ServerType.ARMA3);
        serverLog = mock(ServerLog.class);
        logFile = mock(File.class);
        when(serverLog.getFile()).thenReturn(logFile);
        when(server.getLog()).thenReturn(serverLog);
        serverRepository = mock(ServerRepository.class);
        when(serverRepository.findById(SERVER_ID)).thenReturn(Optional.of(server));
        pathsFactory = mock(PathsFactory.class);
        executable = mock(File.class);
        when(pathsFactory.getServerExecutableWithFallback(ServerType.ARMA3)).thenReturn(executable);
        processCreator = mock(ServerProcessCreator.class);
        process = mock(Process.class);
        when(processCreator.startProcessWithRedirectedOutput(any(), any(), any())).thenReturn(process);

        serverProcess = new ServerProcess(SERVER_ID);
        serverProcess.setServerProcessCreator(processCreator);
        serverProcess.setServerRepository(serverRepository);
        serverProcess.setPathsFactory(pathsFactory);
    }

    @Test
    void getServerId_whenCalled_thenReturnsServerId() {
        assertThat(serverProcess.getServerId()).isEqualTo(SERVER_ID);
    }

    @Test
    void start_whenServerIsStarted_thenNewProcessIsCreatedAndReturned() throws IOException {
        List<String> parameters = List.of("-test=parameter");
        when(server.getLaunchParameters()).thenReturn(parameters);

        Process actualProcess = serverProcess.start();

        assertThat(actualProcess).isEqualTo(actualProcess);
        verify(processCreator).startProcessWithRedirectedOutput(executable, parameters, logFile);
    }

    @Test
    void start_whenServerIsStarted_thenConfigFilesAreGenerated() {
        ServerConfig config1 = mock(ServerConfig.class);
        ServerConfig config2 = mock(ServerConfig.class);
        when(server.getConfigFiles()).thenReturn(List.of(config1, config2));

        serverProcess.start();

        verify(config1).generateIfNecessary();
        verify(config2).generateIfNecessary();
    }

    @Test
    void start_whenServerIsStarted_thenServerLogIsPrepared() {
        serverProcess.start();

        verify(serverLog).prepare();
    }
}