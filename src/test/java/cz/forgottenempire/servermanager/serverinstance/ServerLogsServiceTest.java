package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.Resource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerLogsServiceTest {
    private static final int LINES_COUNT = 10;
    public static final String LOG_NAME = "ARMA3_1.log";
    public static final long SERVER_ID = 1L;
    public static final int LINES_COUNT_OVER_LOG_SIZE = 15;

    @TempDir
    private Path tempDir;
    private PathsFactory pathsFactory;
    private ServerLogsService serverLogsService;
    private Server server;
    private File testLogFile;

    @BeforeEach
    void setUp() throws IOException {
        server = new Arma3Server();
        server.setId(SERVER_ID);
        server.setType(ServerType.ARMA3);

        testLogFile = tempDir.resolve(LOG_NAME).toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(testLogFile));
        writer.write(getTestLog());
        writer.close();

        pathsFactory = mock(PathsFactory.class);
        when(pathsFactory.getServerLogFile(ServerType.ARMA3, SERVER_ID)).thenReturn(testLogFile);

        serverLogsService = new ServerLogsService(pathsFactory);
    }

    @Test
    void whenGetLastLinesFromServerLog_thenLastLinesReturned() {
        String log = serverLogsService.getLastLinesFromServerLog(server, LINES_COUNT);

        assertThat(log).isNotEmpty();
        assertThat(log.lines().toList()).hasSize(LINES_COUNT);
        assertThat(log).isEqualTo(
                """
                        Line 4
                        Line 5
                        Line 6
                        Line 7
                        Line 8
                        Line 9
                        Line 10
                        Line 11
                        Line 12
                        Line 13
                        """
        );
    }

    @Test
    void whenLogFileDoesNotExist_thenEmptyStringReturned() {
        File mockedFile = mock(File.class);
        when(mockedFile.exists()).thenReturn(false);
        when(pathsFactory.getServerLogFile(ServerType.ARMA3, SERVER_ID)).thenReturn(mockedFile);

        String log = serverLogsService.getLastLinesFromServerLog(server, LINES_COUNT);

        assertThat(log).isEmpty();
    }
    @Test
    void whenGetLastLinesFromServerLogAndMoreLinesRequestedThanInLog_thenWholeLogReturned() {
        String log = serverLogsService.getLastLinesFromServerLog(server, LINES_COUNT_OVER_LOG_SIZE);

        assertThat(log).isNotEmpty();
        assertThat(log.lines().toList()).hasSize(getTestLog().lines().toList().size());
        assertThat(log).isEqualTo(getTestLog() + "\n");
    }

    @Test
    void whenGetLogFileAsResourceAndLogExists_thenResourceReturned() throws IOException {
        Resource logFileResource = serverLogsService.getLogFileAsResource(server);

        assertThat(logFileResource).isNotNull();
        assertThat(logFileResource.exists()).isTrue();
        assertThat(logFileResource.getFile()).isEqualTo(testLogFile);
    }

    private String getTestLog() {
        return """
                Line 1
                Line 2
                Line 3
                Line 4
                Line 5
                Line 6
                Line 7
                Line 8
                Line 9
                Line 10
                Line 11
                Line 12
                Line 13""";
    }
}