package cz.forgottenempire.arma3servergui.util;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class SteamCmdWrapper {
    @Value("${steamcmd.path}")
    private String steamCmdPath;

    public SteamCmdWrapper(String steamCmdPath) {
        this.steamCmdPath = steamCmdPath;
    }

    public int execute(List<String> arguments) throws IOException, InterruptedException {
        List<String> commands = new ArrayList<>();
        commands.add(steamCmdPath);
        commands.addAll(arguments);

        Process process = new ProcessBuilder()
                .command(commands)
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        return process.waitFor();
    }
}
