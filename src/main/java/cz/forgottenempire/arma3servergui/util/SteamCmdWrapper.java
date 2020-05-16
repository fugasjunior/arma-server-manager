package cz.forgottenempire.arma3servergui.util;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
@Slf4j
public class SteamCmdWrapper {
    @Value("${steamcmd.path}")
    private String steamCmdPath;

    @Value("${steamcmd.logDir}")
    private String logDir;

    public SteamCmdWrapper(String steamCmdPath) {
        this.steamCmdPath = steamCmdPath;
    }

    public synchronized int execute(List<String> arguments) throws IOException, InterruptedException {
        List<String> commands = new ArrayList<>();
        commands.add(steamCmdPath);
        commands.addAll(arguments);

        File logFile = new File(logDir + File.separatorChar + "out.log");
        LogUtils.prepareLogFile(logFile);

        ProcessBuilder pb = new ProcessBuilder()
                .command(commands);
        if (logFile.exists()) {
            pb.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
        }

        Process process = pb.start();
        return process.waitFor();
    }
}
