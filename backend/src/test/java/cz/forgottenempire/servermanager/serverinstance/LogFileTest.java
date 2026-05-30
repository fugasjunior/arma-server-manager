package cz.forgottenempire.servermanager.serverinstance;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class LogFileTest {

    private static final int MAX_FILES = 3;

    @TempDir
    private Path tempDir;

    private LogFile logFile;
    private File logFileOnDisk;

    @BeforeEach
    void setUp() {
        logFileOnDisk = tempDir.resolve("test.log").toFile();
        logFile = new LogFile(logFileOnDisk);
    }

    @Test
    void rotate_whenFileDoesNotExist_doesNothing() {
        logFile.rotate(MAX_FILES);
        assertThat(logFileOnDisk).doesNotExist();
    }

    @Test
    void rotate_whenFileIsEmpty_doesNothing() throws IOException {
        logFileOnDisk.createNewFile();
        logFile.rotate(MAX_FILES);
        assertThat(logFileOnDisk).exists();
        assertThat(logFileOnDisk).isEmpty();
    }

    @Test
    void rotate_whenFileExists_createsArchive() throws IOException {
        Files.writeString(logFileOnDisk.toPath(), "log content");
        logFile.rotate(MAX_FILES);

        assertThat(logFileOnDisk).isEmpty();
        assertThat(archiveFile(1)).hasContent("log content");
        assertThat(archiveFile(2)).doesNotExist();
    }

    @Test
    void rotate_shiftsExistingArchives() throws IOException {
        // Create initial archives
        Files.writeString(archiveFile(1).toPath(), "old content 1");
        Files.writeString(archiveFile(2).toPath(), "old content 2");
        Files.writeString(logFileOnDisk.toPath(), "new content");

        logFile.rotate(MAX_FILES);

        assertThat(archiveFile(1)).hasContent("new content");
        assertThat(archiveFile(2)).hasContent("old content 1");
        assertThat(archiveFile(3)).hasContent("old content 2");
        assertThat(logFileOnDisk).isEmpty();
    }

    @Test
    void rotate_deletesOldestArchiveWhenExceedsMaxFiles() throws IOException {
        Files.writeString(archiveFile(1).toPath(), "content 1");
        Files.writeString(archiveFile(2).toPath(), "content 2");
        Files.writeString(archiveFile(3).toPath(), "content 3");
        Files.writeString(logFileOnDisk.toPath(), "new content");

        logFile.rotate(MAX_FILES);

        assertThat(archiveFile(1)).hasContent("new content");
        assertThat(archiveFile(2)).hasContent("content 1");
        assertThat(archiveFile(3)).hasContent("content 2");
        assertThat(archiveFile(4)).doesNotExist();
    }

    @Test
    void prepare_rotatesBeforeCreatingFile() throws IOException {
        Files.writeString(logFileOnDisk.toPath(), "old content");

        logFile.prepare(MAX_FILES);

        assertThat(logFileOnDisk).isEmpty();
        assertThat(archiveFile(1)).hasContent("old content");
    }

    @Test
    void prepare_createsFileIfNotExists() {
        logFile.prepare(MAX_FILES);

        assertThat(logFileOnDisk).exists();
        assertThat(logFileOnDisk).isEmpty();
    }

    @Test
    void prepare_createsParentDirectories() {
        logFileOnDisk = tempDir.resolve("subdir1/subdir2/test.log").toFile();
        logFile = new LogFile(logFileOnDisk);

        logFile.prepare(MAX_FILES);

        assertThat(logFileOnDisk).exists();
        assertThat(logFileOnDisk.getParentFile()).isDirectory();
    }

    private File archiveFile(int index) {
        return new File(logFileOnDisk.getPath() + "." + index);
    }
}
