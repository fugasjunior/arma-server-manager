package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LogRotationServiceTest {

    @TempDir
    private Path tempDir;

    private LogRotationService rotationService;
    private PathsFactory pathsFactory;

    @BeforeEach
    void setUp() {
        pathsFactory = mock(PathsFactory.class);
        when(pathsFactory.getLogsBasePath()).thenReturn(tempDir);

        LogRotationProperties props = new LogRotationProperties();
        props.setMaxFiles(3);
        props.setMaxSizeMb(1);
        rotationService = new LogRotationService(pathsFactory, props);
    }

    @Test
    void checkAndRotate_whenLogsDirectoryDoesNotExist_doesNothing() {
        Path nonExistentDir = tempDir.resolve("nonexistent");
        when(pathsFactory.getLogsBasePath()).thenReturn(nonExistentDir);

        assertThatNoException().isThrownBy(() -> rotationService.checkAndRotate());
    }

    @Test
    void checkAndRotate_whenNoLogFiles_doesNothing() throws IOException {
        Files.writeString(tempDir.resolve("readme.txt"), "not a log");

        rotationService.checkAndRotate();

        assertThat(tempDir.resolve("readme.txt")).exists();
    }

    @Test
    void checkAndRotate_whenLogFileBelowThreshold_doesNotRotate() throws IOException {
        Files.writeString(tempDir.resolve("test.log"), "small");

        rotationService.checkAndRotate();

        assertThat(tempDir.resolve("test.log")).hasContent("small");
        assertThat(tempDir.resolve("test.log.1")).doesNotExist();
    }

    @Test
    void checkAndRotate_ignoresArchiveFiles() throws IOException {
        Files.writeString(tempDir.resolve("test.log"), "x".repeat(150));
        Files.writeString(tempDir.resolve("test.log.1"), "archive");

        rotationService.checkAndRotate();

        // test.log.1 should not be considered for rotation
        assertThat(tempDir.resolve("test.log.1")).hasContent("archive");
    }

    @Test
    void checkAndRotate_rotatesMultipleOversizedFiles() throws IOException {
        Files.writeString(tempDir.resolve("server1.log"), "x".repeat(2000000)); // > 1 MB
        Files.writeString(tempDir.resolve("server2.log"), "y".repeat(2000000)); // > 1 MB
        Files.writeString(tempDir.resolve("server3.log"), "z".repeat(100));     // < 1 MB

        rotationService.checkAndRotate();

        assertThat(tempDir.resolve("server1.log").toFile()).isEmpty();
        assertThat(tempDir.resolve("server1.log.1")).exists();
        assertThat(tempDir.resolve("server2.log").toFile()).isEmpty();
        assertThat(tempDir.resolve("server2.log.1")).exists();
        assertThat(tempDir.resolve("server3.log")).hasContent("z".repeat(100));
    }

    @Test
    void checkAndRotate_maintainsMaxFilesRetention() throws IOException {
        LogRotationProperties props = new LogRotationProperties();
        props.setMaxFiles(2);
        props.setMaxSizeMb(1);
        LogRotationService service = new LogRotationService(pathsFactory, props);

        Files.createFile(tempDir.resolve("test.log"));
        Files.createFile(tempDir.resolve("test.log.1"));
        Files.createFile(tempDir.resolve("test.log.2"));
        Files.writeString(tempDir.resolve("test.log"), "x".repeat(2000000)); // Trigger rotation

        service.checkAndRotate();

        assertThat(tempDir.resolve("test.log").toFile()).isEmpty();
        assertThat(tempDir.resolve("test.log.1")).exists();
        assertThat(tempDir.resolve("test.log.2")).exists();
        assertThat(tempDir.resolve("test.log.3")).doesNotExist(); // Deleted (exceeds maxFiles)
    }

    @Test
    void checkAndRotate_rotatesAdditionalServerLogsInSubdirectories() throws IOException {
        Path serverLogsDir = tempDir.resolve("additional_server");
        Files.createDirectories(serverLogsDir);
        Files.writeString(serverLogsDir.resolve("log.txt"), "x".repeat(2000000)); // > 1 MB

        rotationService.checkAndRotate();

        assertThat(serverLogsDir.resolve("log.txt").toFile()).isEmpty();
        assertThat(serverLogsDir.resolve("log.txt.1")).exists();
    }
}
