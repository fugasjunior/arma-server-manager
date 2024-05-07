package cz.forgottenempire.servermanager.serverinstance.headlessclient;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;

class HeadlessClientTest {

    public static final long SERVER_ID = 1L;
    public static final int HEADLESS_CLIENT_ID = 10;
    public static final int SERVER_PORT = 2302;
    public static final String SERVER_PASSWORD = "hunter2";

    private File logFile;
    private File serverExecutable;
    private Arma3Server server;
    private ServerProcessCreator serverProcessCreator;

    private HeadlessClient headlessClient;

    @BeforeEach
    void setUp() {
        server = mock(Arma3Server.class, withSettings().stubOnly());
        logFile = mock(File.class, withSettings().stubOnly());
        serverExecutable = mock(File.class, withSettings().stubOnly());
        serverProcessCreator = mock(ServerProcessCreator.class);

        PathsFactory pathsFactory = mock(PathsFactory.class);
        when(pathsFactory.getHeadlessClientLogFile(SERVER_ID, HEADLESS_CLIENT_ID)).thenReturn(logFile);
        when(pathsFactory.getServerExecutableWithFallback(ServerType.ARMA3)).thenReturn(serverExecutable);

        headlessClient = new HeadlessClient(HEADLESS_CLIENT_ID, server);
        headlessClient.setPathsFactory(pathsFactory);
        headlessClient.setServerProcessCreator(serverProcessCreator);
    }

    @Test
    void whenHeadlessClientIsStarted_thenProcessCreatedWithCorrectParameters() throws IOException {
        when(server.getId()).thenReturn(SERVER_ID);
        when(server.getPort()).thenReturn(SERVER_PORT);

        headlessClient.start();

        List<String> expectedParameters = List.of("-client", "-connect=127.0.0.1:2302");
        verify(serverProcessCreator).startProcessWithRedirectedOutput(serverExecutable, expectedParameters, logFile);
    }

    @Test
    void whenHeadlessClientIsStartedWithPasswordProtectedServer_thenProcessCreatedWithPasswordParameter() throws IOException {
        when(server.getId()).thenReturn(SERVER_ID);
        when(server.getPort()).thenReturn(SERVER_PORT);
        when(server.getPassword()).thenReturn(SERVER_PASSWORD);

        headlessClient.start();

        List<String> expectedParameters = List.of("-client", "-connect=127.0.0.1:2302", "-password=" + SERVER_PASSWORD);
        verify(serverProcessCreator).startProcessWithRedirectedOutput(serverExecutable, expectedParameters, logFile);
    }

    @Test
    void whenHeadlessClientIsStartedWithClientMods_thenModsArePresentInParameters() throws IOException {
        when(server.getId()).thenReturn(SERVER_ID);
        when(server.getPort()).thenReturn(SERVER_PORT);
        when(server.getClientModsAsParameters()).thenReturn(Stream.of("-mod=@CBA", "-mod=@ACE3"));

        headlessClient.start();

        List<String> expectedParameters = List.of("-client", "-connect=127.0.0.1:2302", "-mod=@CBA", "-mod=@ACE3");
        verify(serverProcessCreator).startProcessWithRedirectedOutput(serverExecutable, expectedParameters, logFile);
    }

    @Test
    void whenHeadlessClientIsStartedWithAdditionalMods_thenModsArePresentInParameters() throws IOException {
        when(server.getId()).thenReturn(SERVER_ID);
        when(server.getPort()).thenReturn(SERVER_PORT);
        when(server.getAdditionalModsAsParameters()).thenReturn(Stream.of("-mod=@CBA", "-mod=@ACE3"));

        headlessClient.start();

        List<String> expectedParameters = List.of("-client", "-connect=127.0.0.1:2302", "-mod=@CBA", "-mod=@ACE3");
        verify(serverProcessCreator).startProcessWithRedirectedOutput(serverExecutable, expectedParameters, logFile);
    }
}