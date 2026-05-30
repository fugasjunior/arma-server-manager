package cz.forgottenempire.servermanager.serverinstance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class LogFile {

    private final File logFile;

    public LogFile(File logFile) {
        this.logFile = logFile;
    }

    public File getFile() {
        return logFile;
    }

    public void prepare(int maxFiles) {
        rotate(maxFiles);
        if (logFile.exists()) {
            return;
        }

        try {
            logFile.getParentFile().mkdirs();
            logFile.createNewFile();
        } catch (IOException e) {
            log.error("Could not create log file {}", logFile.getAbsolutePath());
        }
    }

    public void rotate(int maxFiles) {
        if (!logFile.exists() || logFile.length() == 0) {
            return;
        }
        deleteOldestArchiveIfExceeded(maxFiles);
        try {
            shiftExistingArchives(maxFiles);
        } catch (IOException e) {
            log.error("Failed to shift log archives for {}, skipping rotation", logFile.getAbsolutePath(), e);
            return;
        }
        archiveCurrentLogFile();
    }

    private void deleteOldestArchiveIfExceeded(int maxFiles) {
        File oldest = archivedFile(maxFiles);
        if (oldest.exists() && !oldest.delete()) {
            log.warn("Could not delete old log archive {}", oldest.getAbsolutePath());
        }
    }

    private void shiftExistingArchives(int maxFiles) throws IOException {
        for (int i = maxFiles - 1; i >= 1; i--) {
            File archive = archivedFile(i);
            if (archive.exists()) {
                Files.move(archive.toPath(), archivedFile(i + 1).toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void archiveCurrentLogFile() {
        try {
            Files.copy(logFile.toPath(), archivedFile(1).toPath(), StandardCopyOption.REPLACE_EXISTING);
            try (RandomAccessFile raf = new RandomAccessFile(logFile, "rw")) {
                raf.setLength(0);
            }
        } catch (IOException e) {
            log.error("Failed to rotate log file {} by copy-truncate", logFile.getAbsolutePath(), e);
        }
    }

    private File archivedFile(int index) {
        return new File(logFile.getPath() + "." + index);
    }

    public String getLastLines(int count) {
        if (!logFile.exists()) {
            return "";
        }

        try {
            return getLastNLines(logFile, count);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Optional<Resource> asResource() {
        if (!logFile.exists()) {
            return Optional.empty();
        }

        try {
            return Optional.of(new FileUrlResource(logFile.getAbsolutePath()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getLastNLines(File file, int n) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(file, "r");

        long filePointer = raf.length() > 2 ? raf.length() - 2 : 0; // offset so there's no null at the end of log
        int lines = 0;
        StringBuilder result = new StringBuilder();

        while (filePointer >= 0 && lines < n) {
            raf.seek(filePointer);
            if (filePointer == 0) {
                // If we're at the beginning of the file, read the last line and exit
                result.insert(0, raf.readLine() + '\n');
                break;
            } else {
                // Otherwise, read the current character and move back one byte
                int currentByte = raf.read();
                filePointer--;
                if (currentByte == '\n') {
                    // If we've found a line ending, read the next line
                    result.insert(0, raf.readLine() + '\n');
                    lines++;
                } else if (currentByte == '\r') {
                    // If we've found a carriage return, read the next byte and check for a line ending
                    filePointer--;
                    raf.seek(filePointer);
                    if (raf.read() == '\n') {
                        // If the next byte is a line feed, read the next line
                        result.insert(0, raf.readLine() + '\n');
                        lines++;
                    }
                }
            }
        }

        raf.close();
        return result.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LogFile logFile = (LogFile) o;
        return Objects.equals(this.logFile, logFile.logFile);
    }

    @Override
    public int hashCode() {
        return Objects.hash(logFile);
    }
}
