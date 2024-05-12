package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.PathsFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
class SteamCmdOutputProcessor {

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

        if (lowerCaseLine.startsWith("success. downloaded item")) {
            Matcher matcher = itemIdFromSuccess.matcher(lowerCaseLine);
            matcher.find();
            String idString = matcher.group(1);
            if (StringUtils.isBlank(idString)) {
                log.error("Failed to parse item ID from line '{}'", lowerCaseLine);
            }

            try {
                long itemId = Long.parseLong(idString);

                Matcher bytesMatcher = bytesFromSuccess.matcher(lowerCaseLine);
                bytesMatcher.find();
                long bytes = Long.parseLong(bytesMatcher.group(1));

                SteamCmdItemInfo itemInfo = new SteamCmdItemInfo(SteamCmdStatus.FINISHED, 100, bytes, bytes);

                log.info("{}: {}", itemId, itemInfo);
            } catch (NumberFormatException e) {
                log.error("Failed to parse item ID '{}' to long", idString, e);
            }
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

    public record SteamCmdItemInfo(SteamCmdStatus status, long progressPercent, long bytesFinished, long bytesDone) {
    }

    public enum SteamCmdStatus {
        FINISHED,
        IN_PROGRESS,
        ERROR
    }
}
