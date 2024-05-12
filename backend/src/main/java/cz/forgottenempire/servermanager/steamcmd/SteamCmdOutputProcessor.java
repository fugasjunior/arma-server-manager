package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.steamcmd.lines.WorkshopItemDownloadSuccessLine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

@Component
@Slf4j
public class SteamCmdOutputProcessor {

    private final PathsFactory pathsFactory;
    private final Pattern itemIdFromSuccess = Pattern.compile("downloaded item (\\d+)");
    private final Pattern bytesFromSuccess = Pattern.compile("\\((\\d+)\\sbytes\\)");

    @Autowired
    SteamCmdOutputProcessor(PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
    }

    String processSteamCmdOutput(InputStream processOutput) throws IOException {
        StringBuilder result = new StringBuilder();

        File logFile = prepareLogFile();

        try (BufferedReader steamCmdOuput = new BufferedReader(new InputStreamReader(processOutput));
             BufferedWriter fileLogger = new BufferedWriter(new FileWriter(logFile, true))) {
            String line;
            while ((line = steamCmdOuput.readLine()) != null) {
                processLine(line);
                result.append(line);
                log.debug(line);
                writeLineIntoLogFile(fileLogger, line);
            }

            writeSeparatorIntoLogFile(fileLogger);
        }

        return result.toString();
    }

    private void processLine(String line) {
        String lowerCaseLine = line.toLowerCase();
        SteamCmdItemInfo itemInfo = null;

        if (lowerCaseLine.startsWith("success. downloaded item")) {
            WorkshopItemDownloadSuccessLine line2 = new WorkshopItemDownloadSuccessLine(lowerCaseLine);
            itemInfo = line2.parseInfo();
        }

        if (itemInfo != null) {
            log.info("{}: {}", itemInfo.itemId(), itemInfo);
        } else {
            log.info("itemInfo was null");
        }
    }

    private File prepareLogFile() throws IOException {
        File logFile = pathsFactory.getSteamCmdLogFile();
        logFile.getParentFile().mkdirs();
        logFile.createNewFile();
        return logFile;
    }

    private static void writeLineIntoLogFile(BufferedWriter fileLogger, String line) throws IOException {
        fileLogger.write(getCurrentTime() + " " + line);
        fileLogger.newLine();
        fileLogger.flush();
    }

    private static void writeSeparatorIntoLogFile(BufferedWriter fileLogger) throws IOException {
        fileLogger.newLine();
        fileLogger.newLine();
        fileLogger.flush();
    }

    public static String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("[HH:mm:ss]");
        return timeFormat.format(new Date());
    }

    public record SteamCmdItemInfo(long itemId, SteamCmdStatus status, long progressPercent, long bytesFinished, long bytesDone) {
    }

    public enum SteamCmdStatus {
        FINISHED,
        IN_PROGRESS,
        ERROR
    }
}
