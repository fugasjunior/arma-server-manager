package cz.forgottenempire.servermanager.steamcmd;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Component
class SteamCmdOutputProcessor {
    String processSteamCmdOutput(InputStream processOutput) throws IOException {
        StringBuilder result = new StringBuilder();

        try (BufferedReader steamCmdOuput = new BufferedReader(new InputStreamReader(processOutput))) {
            String line;
            while ((line = steamCmdOuput.readLine()) != null) {
                result.append(line);
            }
        }

        return result.toString();
    }
}