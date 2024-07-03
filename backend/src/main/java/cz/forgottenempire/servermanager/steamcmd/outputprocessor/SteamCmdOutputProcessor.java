package cz.forgottenempire.servermanager.steamcmd.outputprocessor;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdJob;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.lines.SteamCmdOutputLine;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.lines.SteamCmdOutputLineFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class SteamCmdOutputProcessor {

    private final PathsFactory pathsFactory;
    private final SteamCmdOutputLineFactory steamCmdOutputLineFactory;
    private final SteamCmdItemInfoRepository itemInfoRepository;

    @Autowired
    SteamCmdOutputProcessor(
            PathsFactory pathsFactory,
            SteamCmdOutputLineFactory steamCmdOutputLineFactory,
            SteamCmdItemInfoRepository itemInfoRepository
    ) {
        this.pathsFactory = pathsFactory;
        this.steamCmdOutputLineFactory = steamCmdOutputLineFactory;
        this.itemInfoRepository = itemInfoRepository;
    }

    public String processSteamCmdOutput(InputStream processOutput, SteamCmdJob job) throws IOException {
        StringBuilder result = new StringBuilder();

        File logFile = prepareLogFile();

        try (BufferedReader steamCmdOuput = new BufferedReader(new InputStreamReader(processOutput));
             BufferedWriter fileLogger = new BufferedWriter(new FileWriter(logFile, true))) {
            String line;
            while ((line = steamCmdOuput.readLine()) != null) {
                processLine(line, job);
                result.append(line);
                log.debug(line);
                writeLineIntoLogFile(fileLogger, line);
            }

            writeSeparatorIntoLogFile(fileLogger);
        }

        return result.toString();
    }

    private void processLine(String line, SteamCmdJob job) {
        for (String normalizedLine : normalizeLine(line)) {
            SteamCmdOutputLine lineObject = steamCmdOutputLineFactory.createSteamCmdOutputLine(normalizedLine, job);

            if (lineObject != null) {
                SteamCmdItemInfo itemInfo = lineObject.parseInfo();
                itemInfoRepository.store(itemInfo.itemId(), itemInfo);
            }
        }
    }

    private static String[] normalizeLine(String line) {
        String normalizedLine = line.toLowerCase().trim();
        String[] lines;
        boolean isTwoLinesWithoutLineBreak = !normalizedLine.startsWith("downloading item") && normalizedLine.contains("downloading item");
        if (isTwoLinesWithoutLineBreak) {
            lines = normalizedLine.split("downloading item");
            lines[1] = "downloading item" + lines[1];
        } else {
            lines = new String[]{normalizedLine};
        }
        return lines;
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
}
