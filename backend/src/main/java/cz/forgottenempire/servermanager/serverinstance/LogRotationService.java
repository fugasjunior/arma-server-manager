package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Service
@Slf4j
public class LogRotationService {

    private static final String LOG_ARCHIVE_FILE_NAME_PATTERN = ".*\\.\\d+$";

    private final PathsFactory pathsFactory;
    private final int maxFiles;
    private final long maxSizeBytes;

    @Autowired
    public LogRotationService(
            PathsFactory pathsFactory,
            LogRotationProperties properties
    ) {
        this.pathsFactory = pathsFactory;
        this.maxFiles = properties.getMaxFiles();
        this.maxSizeBytes = properties.getMaxSizeMb() * 1024 * 1024;
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    public void checkAndRotate() {
        Path logsPath = pathsFactory.getLogsBasePath();
        if (!logsPath.toFile().exists()) {
            return;
        }

        try (Stream<Path> paths = Files.walk(logsPath)) {
            paths.filter(Files::isRegularFile)
                .filter(p -> {
                    String name = p.getFileName().toString();
                    return (name.endsWith(".log") || name.equals("log.txt"))
                        && !name.matches(LOG_ARCHIVE_FILE_NAME_PATTERN);
                })
                .forEach(p -> {
                    var logFile = p.toFile();
                    if (logFile.length() > maxSizeBytes) {
                        log.info("Log file {} exceeds size threshold ({} MB), rotating", logFile.getName(), maxSizeBytes / (1024 * 1024));
                        new LogFile(logFile).rotate(maxFiles);
                    }
                });
        } catch (IOException e) {
            log.error("Failed to scan log directory for rotation", e);
        }
    }
}
