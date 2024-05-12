package cz.forgottenempire.servermanager.steamcmd;

import com.google.common.base.Strings;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.steamauth.SteamAuth;
import cz.forgottenempire.servermanager.steamauth.SteamAuthService;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class SteamCmdExecutor {

    private static final String STEAM_CREDENTIALS_PLACEHOLDER = "<{STEAM_CREDENTIALS_PLACEHOLDER}>";
    private static final List<String> ERROR_KEYWORDS = Arrays.asList("error", "failure", "failed");
    private static final int EXIT_CODE_TIMEOUT_LINUX = 134;
    private static final int EXIT_CODE_TIMEOUT_WINDOWS = 10;
    private static final int MAX_ATTEMPTS = 10;

    private final File steamCmdFile;
    private final SteamAuthService steamAuthService;
    private final ProcessFactory processFactory;
    private final SteamCmdOutputProcessor steamCmdOutputProcessor;

    @Autowired
    public SteamCmdExecutor(
            @Value("${steamcmd.path}") String steamCmdFilePath,
            SteamAuthService steamAuthService,
            ProcessFactory processFactory,
            SteamCmdOutputProcessor steamCmdOutputProcessor
    ) {
        this.steamAuthService = steamAuthService;
        this.processFactory = processFactory;
        this.steamCmdOutputProcessor = steamCmdOutputProcessor;
        steamCmdFile = new File(steamCmdFilePath);
        if (!steamCmdFile.exists()) {
            throw new IllegalStateException("Invalid path to SteamCMD executable given");
        }
    }

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public void processJob(SteamCmdJob job, CompletableFuture<SteamCmdJob> future) {
        executor.submit(() -> {
            execute(job);
            future.complete(job);
        });
    }

    private void execute(SteamCmdJob job) {
        try {
            int attempts = 0;
            int exitCode;
            StringBuilder output = new StringBuilder();

            do {
                attempts++;
                Process process = processFactory.startProcessWithUnbufferedOutput(steamCmdFile, getCommands(job.getSteamCmdParameters()));
                steamCmdOutputProcessor.processSteamCmdOutput(output, process.getInputStream());
                exitCode = process.waitFor();
            } while (attempts < MAX_ATTEMPTS && exitedDueToTimeout(exitCode));

            handleProcessResult(output.toString(), job);
        } catch (SteamAuthNotSetException e) {
            log.error("SteamAuth is not set up");
            job.setErrorStatus(ErrorStatus.WRONG_AUTH);
        } catch (IOException e) {
            log.error("SteamCMD job failed due to an IO error", e);
            job.setErrorStatus(ErrorStatus.IO);
        } catch (Exception e) {
            log.error("SteamCMD job failed", e);
            job.setErrorStatus(ErrorStatus.GENERIC);
        }
    }

    private boolean exitedDueToTimeout(int exitCode) {
        return exitCode == EXIT_CODE_TIMEOUT_LINUX || exitCode == EXIT_CODE_TIMEOUT_WINDOWS;
    }

    private List<String> getCommands(SteamCmdParameters parameters) {
        List<String> commands = new ArrayList<>();
        parameters.get().forEach(parameter -> {
            if (parameter.contains(STEAM_CREDENTIALS_PLACEHOLDER)) {
                commands.add(parameter.replace(STEAM_CREDENTIALS_PLACEHOLDER, getAuthString()));
            } else {
                commands.add(parameter);
            }
        });
        return commands;
    }

    private void handleProcessResult(String result, SteamCmdJob job) {
        // SteamCMD doesn't provide the user with any proper exit values or standard format for error messages.
        String errorLine = result.lines()
                .map(String::toLowerCase)
                .map(this::removeParametersFromOutputLine)
                // Issue #69 missing steamservice.so and libSDL3.so.0 caused the job to be marked as failed
                // TODO find better solution to determine the job result
                .filter(line -> !line.contains("cannot open shared object file"))
                .filter(this::containsErrorKeyword)
                .findFirst()
                .orElse(null);

        if (errorLine == null) {
            return;
        }

        log.error("SteamCmd failed due to: '{}'", errorLine);
        job.setErrorStatus(ErrorStatus.GENERIC);

        dumpErrorOutputToLog(result);

        String[] loginRelatedErrors = new String[]{"login", "expired", "account logon denied", "two-factor code mismatch", "invalid password"};
        if (Arrays.stream(loginRelatedErrors).anyMatch(errorLine::contains)) {
            job.setErrorStatus(ErrorStatus.WRONG_AUTH);
        }
        if (errorLine.contains("no subscription")) {
            job.setErrorStatus(ErrorStatus.NO_SUBSCRIPTION);
        }
        if (errorLine.contains("no match")) {
            job.setErrorStatus(ErrorStatus.NO_MATCH);
        }
        if (errorLine.contains("i/o operation") || errorLine.contains("failed to write file")) {
            job.setErrorStatus(ErrorStatus.IO);
        }
        if (errorLine.contains("rate limit exceeded")) {
            job.setErrorStatus(ErrorStatus.RATE_LIMIT);
        }
    }

    private void dumpErrorOutputToLog(String result) {
        log.error("======== SteamCMD ERROR OUTPUT START ======== ");
        log.error(result);
        log.error("======== SteamCMD ERROR OUTPUT END ======== ");
    }

    private String getAuthString() {
        SteamAuth steamAuth = steamAuthService.getAuthAccount();
        if (steamAuth.getUsername() == null || steamAuth.getPassword() == null) {
            throw new SteamAuthNotSetException();
        }

        String authString = steamAuth.getUsername() + " " + steamAuth.getPassword();
        if (!Strings.isNullOrEmpty(steamAuth.getSteamGuardToken())) {
            authString += " " + steamAuth.getSteamGuardToken();
        }
        return authString;
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
}
