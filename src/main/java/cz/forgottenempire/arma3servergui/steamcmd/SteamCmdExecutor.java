package cz.forgottenempire.arma3servergui.steamcmd;

import cz.forgottenempire.arma3servergui.steamcmd.exceptions.GenericErrorException;
import cz.forgottenempire.arma3servergui.steamcmd.exceptions.IOOperationException;
import cz.forgottenempire.arma3servergui.steamcmd.exceptions.LoginException;
import cz.forgottenempire.arma3servergui.steamcmd.exceptions.NoMatchException;
import cz.forgottenempire.arma3servergui.steamcmd.exceptions.NoSubscriptionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// TODO make package-private
public class SteamCmdExecutor {

    private final File steamCmdFile;
    private SteamCmdParameters parameters;

    private static final List<String> ERROR_KEYWORDS = Arrays.asList("error", "failure", "failed");
    private static final int EXIT_CODE_TIMEOUT_LINUX = 134;
    private static final int EXIT_CODE_TIMEOUT_WINDOWS = 10;
    private static final int MAX_ATTEMPTS = 10;

    public SteamCmdExecutor(File steamCmdFile) {
        this.steamCmdFile = steamCmdFile;
    }

    public SteamCmdExecutor(File steamCmdFile, SteamCmdParameters parameters) {
        this(steamCmdFile);
        this.parameters = parameters;
    }

    public synchronized void execute() throws IOException, InterruptedException {
        List<String> commands = new ArrayList<>();
        commands.add(steamCmdFile.getAbsolutePath());
        commands.addAll(parameters.getParameters());

        InputStream resultStream = null;
        int attempts = 0;
        int exitCode;
        do {
            attempts++;
            Process process = new ProcessBuilder()
                    .command(commands)
                    .start();

            resultStream = process.getInputStream();
            exitCode = process.waitFor();
        } while (attempts < MAX_ATTEMPTS &&
                (exitCode == EXIT_CODE_TIMEOUT_LINUX || exitCode == EXIT_CODE_TIMEOUT_WINDOWS));

        handleProcessResult(resultStream);
    }

    private void handleProcessResult(InputStream cmdOutput) {
        // SteamCMD doesn't provide the user with any proper exit values or standard format for error messages.
        String errorLine = new BufferedReader(new InputStreamReader(cmdOutput)).lines()
                .map(String::toLowerCase)
                .map(this::removeParametersFromOutputLine)
                .filter(this::containsErrorKeyword)
                .findFirst()
                .orElse(null);

        if (errorLine != null) {
            if (errorLine.contains("login")) {
                throw new LoginException(errorLine);
            }
            if (errorLine.contains("no subscription")) {
                throw new NoSubscriptionException(errorLine);
            }
            if (errorLine.contains("no match")) {
                throw new NoMatchException(errorLine);
            }
            if (errorLine.contains("i/o operation")) {
                throw new IOOperationException(errorLine);
            }
            throw new GenericErrorException(errorLine);
        }
    }

    private String removeParametersFromOutputLine(String line) {
        return line.replace("\"@shutdownonfailedcommand\" = \"1\"", "")
                .replace("\"@nopromptforpassword\" = \"1\"", "");
    }

    private boolean containsErrorKeyword(String targetString) {
        return SteamCmdExecutor.ERROR_KEYWORDS.stream()
                .map(String::toLowerCase)
                .anyMatch(keyword -> targetString.contains(keyword) && !targetString.contains("warning"));
    }

    public void setParameters(SteamCmdParameters parameters) {
        this.parameters = parameters;
    }
}
