package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;

class ServerLogsServiceTest {
    private static final int LINES_COUNT = 10;
    public static final String LOG_NAME = "ARMA3_1.log";
    public static final long SERVER_ID = 1L;

    @TempDir
    private Path tempDir;

    @Test
    void whenGetLastLinesFromServerLog_thenLastLinesReturned() throws IOException {
        File testLogFile = tempDir.resolve(LOG_NAME).toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(testLogFile));
        writer.write(getTestLog());
        writer.close();

        PathsFactory pathsFactory = mock(PathsFactory.class);
        when(pathsFactory.getServerLogFile(ServerType.ARMA3, SERVER_ID)).thenReturn(testLogFile);

        ServerLogsService serverLogsService = new ServerLogsService(pathsFactory);
        Server server = new Arma3Server();
        server.setId(SERVER_ID);
        server.setType(ServerType.ARMA3);
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