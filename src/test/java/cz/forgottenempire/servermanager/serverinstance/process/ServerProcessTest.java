package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.serverinstance.ServerInstanceInfo;
import cz.forgottenempire.servermanager.serverinstance.ServerLog;
import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ServerProcessTest {

    private static final long SERVER_ID = 1L;
    public static final int MAX_PLAYERS = 32;

    private ServerProcess serverProcess;
    private Server server;
    private File logFile;
    private ServerLog serverLog;
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
        ServerRepository serverRepository = mock(ServerRepository.class);
        when(serverRepository.findById(SERVER_ID)).thenReturn(Optional.of(server));
        PathsFactory pathsFactory = mock(PathsFactory.class);
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

        assertThat(actualProcess).isEqualTo(process);
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

    @Test
    void start_whenServerFailsToStart_thenNullIsReturned() throws IOException {
        when(processCreator.startProcessWithRedirectedOutput(any(), any(), any())).thenThrow(IOException.class);

        Process actualProcess = serverProcess.start();

        assertThat(serverProcess.getInstanceInfo()).isNull();
        assertThat(actualProcess).isNull();
    }

    @Test
    void start_whenServerIsAlreadyRunning_thenExistingProcessIsReturned() throws IOException {
        serverProcess.start();
        verify(processCreator).startProcessWithRedirectedOutput(any(), any(), any());
        when(process.isAlive()).thenReturn(true);

        Process actualProcess = serverProcess.start();

        assertThat(actualProcess).isEqualTo(process);
        verifyNoMoreInteractions(processCreator);
    }

    @Test
    void stop_whenServerIsRunning_thenProcessIsDestroyed() {
        serverProcess.start();
        when(process.isAlive()).thenReturn(true);

        serverProcess.stop();

        verify(process).destroy();
    }

    @Test
    void stop_whenProcessDoesNotExist_thenNoActionIsTaken() {
        serverProcess.stop();

        verifyNoInteractions(process);
    }

    @Test
    void stop_whenProcessIsNotAlive_thenNoActionIsTaken() {
        serverProcess.start();
        verify(process).pid();
        when(process.isAlive()).thenReturn(false);

        verifyNoMoreInteractions(process);
    }

    @Test
    void isAlive_whenProcessIsRunning_thenReturnsTrue() {
        serverProcess.start();
        when(process.isAlive()).thenReturn(true);

        assertThat(process.isAlive()).isTrue();
    }

    @Test
    void isAlive_whenProcessDoesNotExist_thenReturnsFalse() {
        assertThat(process.isAlive()).isFalse();
    }

    @Test
    void isAlive_whenProcessIsNotAlive_thenReturnsFalse() {
        serverProcess.start();
        when(process.isAlive()).thenReturn(false);

        assertThat(process.isAlive()).isFalse();
    }

    @Test
    void getInstanceInfo_whenServerIsStarted_thenInstanceInfoIsCreated() {
        LocalDateTime beforeCreation = LocalDateTime.now();
        when(server.getMaxPlayers()).thenReturn(MAX_PLAYERS);
        serverProcess.start();

        ServerInstanceInfo instanceInfo = serverProcess.getInstanceInfo();

        assertThat(instanceInfo).isNotNull();
        assertThat(instanceInfo.getStartedAt()).isBetween(beforeCreation, LocalDateTime.now());
        assertThat(instanceInfo.getMaxPlayers()).isEqualTo(MAX_PLAYERS);
    }

    @Test
    void getInstanceInfo_whenServerIsStopped_thenInstanceInfoIsReset() {
        serverProcess.start();
        serverProcess.stop();

        ServerInstanceInfo instanceInfo = serverProcess.getInstanceInfo();

        assertThat(instanceInfo).isNotNull();
        assertThat(instanceInfo.getStartedAt()).isNull();
        assertThat(instanceInfo.getMaxPlayers()).isZero();
    }
}