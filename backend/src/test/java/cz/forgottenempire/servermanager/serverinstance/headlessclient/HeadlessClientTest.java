package cz.forgottenempire.servermanager.serverinstance.headlessclient;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessCreator;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

class HeadlessClientTest {

    public static final long SERVER_ID = 1L;
    public static final int HEADLESS_CLIENT_ID = 10;
    public static final int SERVER_PORT = 2302;
    public static final String SERVER_PASSWORD = "hunter2";

    @Test
    void whenHeadlessClientIsStarted_thenProcessCreatedWithCorrectParameters() throws IOException {
        Arma3Server server = mock(Arma3Server.class, withSettings().stubOnly());
        when(server.getId()).thenReturn(SERVER_ID);
        when(server.getPort()).thenReturn(SERVER_PORT);

        HeadlessClient headlessClient = new HeadlessClient(HEADLESS_CLIENT_ID, server);
        PathsFactory pathsFactory = mock(PathsFactory.class);

        File logFile = mock(File.class, withSettings().stubOnly());
        when(pathsFactory.getHeadlessClientLogFile(SERVER_ID, HEADLESS_CLIENT_ID)).thenReturn(logFile);

        File serverExecutable = mock(File.class, withSettings().stubOnly());
        when(pathsFactory.getServerExecutableWithFallback(ServerType.ARMA3)).thenReturn(serverExecutable);

        headlessClient.setPathsFactory(pathsFactory);
        ServerProcessCreator serverProcessCreator = mock(ServerProcessCreator.class);
        headlessClient.setServerProcessCreator(serverProcessCreator);

        headlessClient.start();

        List<String> expectedParameters = List.of("-client", "-connect=127.0.0.1:2302");
        verify(serverProcessCreator).startProcessWithRedirectedOutput(serverExecutable, expectedParameters, logFile);
    }
    @Test
    void whenHeadlessClientIsStartedWithPasswordProtectedServer_thenProcessCreatedWithPasswordParameter() throws IOException {
        Arma3Server server = mock(Arma3Server.class, withSettings().stubOnly());
        when(server.getId()).thenReturn(SERVER_ID);
        when(server.getPort()).thenReturn(SERVER_PORT);
        when(server.getPassword()).thenReturn(SERVER_PASSWORD);

        HeadlessClient headlessClient = new HeadlessClient(HEADLESS_CLIENT_ID, server);
        PathsFactory pathsFactory = mock(PathsFactory.class);

        File logFile = mock(File.class, withSettings().stubOnly());
        when(pathsFactory.getHeadlessClientLogFile(SERVER_ID, HEADLESS_CLIENT_ID)).thenReturn(logFile);

        File serverExecutable = mock(File.class, withSettings().stubOnly());
        when(pathsFactory.getServerExecutableWithFallback(ServerType.ARMA3)).thenReturn(serverExecutable);

        headlessClient.setPathsFactory(pathsFactory);
        ServerProcessCreator serverProcessCreator = mock(ServerProcessCreator.class);
        headlessClient.setServerProcessCreator(serverProcessCreator);

        headlessClient.start();

        List<String> expectedParameters = List.of("-client", "-connect=127.0.0.1:2302", "-password=" + SERVER_PASSWORD);
        verify(serverProcessCreator).startProcessWithRedirectedOutput(serverExecutable, expectedParameters, logFile);
    }
}