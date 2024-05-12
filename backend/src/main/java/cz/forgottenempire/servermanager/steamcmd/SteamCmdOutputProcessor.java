package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.PathsFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
class SteamCmdOutputProcessor {

    private final PathsFactory pathsFactory;

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
                result.append(line);
                writeLineIntoLogFile(fileLogger, line);
            }
        }

        return result.toString();
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

    public static String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("[HH:mm:ss]");
        return timeFormat.format(new Date());
    }
}
