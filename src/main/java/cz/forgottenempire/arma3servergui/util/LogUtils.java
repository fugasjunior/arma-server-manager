package cz.forgottenempire.arma3servergui.util;

import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {

    public static void prepareLogFile(File logFile) {
        if (!logFile.exists()) {
            try {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            } catch (IOException e) {
                log.error("Could not create log file {}", logFile.getAbsolutePath());
            }
        }
    }
}
