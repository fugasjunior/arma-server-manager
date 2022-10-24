package cz.forgottenempire.arma3servergui.steamcmd;

import com.google.common.base.Strings;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob.JobStatus;
import cz.forgottenempire.arma3servergui.steamcmd.repositories.SteamCmdJobRepository;
import cz.forgottenempire.arma3servergui.system.entities.SteamAuth;
import cz.forgottenempire.arma3servergui.system.repositories.SteamAuthRepository;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
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
public class SteamCmdExecutor {


    private static final String STEAM_CREDENTIALS_PLACEHOLDER = "<{STEAM_CREDENTIALS_PLACEHOLDER}>";

    private static final List<String> ERROR_KEYWORDS = Arrays.asList("error", "failure", "failed");
    private static final int EXIT_CODE_TIMEOUT_LINUX = 134;
    private static final int EXIT_CODE_TIMEOUT_WINDOWS = 10;
    private static final int MAX_ATTEMPTS = 10;

    private final File steamCmdFile;

    private final SteamCmdJobRepository jobRepository;

    private final SteamAuthRepository steamAuthRepository;

    @Autowired
    public SteamCmdExecutor(
            @Value("${steamcmd.path}") String steamCmdFilePath,
            SteamCmdJobRepository jobRepository,
            SteamAuthRepository steamAuthRepository
    ) {
        this.jobRepository = jobRepository;
        this.steamAuthRepository = steamAuthRepository;

        steamCmdFile = new File(steamCmdFilePath);
        if (!steamCmdFile.exists()) {
            throw new IllegalStateException("Invalid path to SteamCMD executable given");
        }
    }

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public void processJob(SteamCmdJob job, CompletableFuture<SteamCmdJob> future) {
        executor.submit(() -> {
            job.setState(JobStatus.RUNNING);
            execute(job);
            job.setFinishedAt(LocalDateTime.now());
            jobRepository.save(job);
            future.complete(job);
        });
    }

    private void execute(SteamCmdJob job) {
        List<String> commands = new ArrayList<>();
        commands.add(steamCmdFile.getAbsolutePath());

        job.getSteamCmdParameters()
                .getParameters()
                .forEach(parameter -> {
                    if (parameter.contains(STEAM_CREDENTIALS_PLACEHOLDER)) {
                        commands.add(parameter.replace(STEAM_CREDENTIALS_PLACEHOLDER, getAuthString()));
                    } else {
                        commands.add(parameter);
                    }
                });

        InputStream resultStream;
        int attempts = 0;
        int exitCode;

        try {
            do {
                attempts++;
                Process process = new ProcessBuilder()
                        .command(commands)
                        .start();

                resultStream = process.getInputStream();
                exitCode = process.waitFor();
            } while (attempts < MAX_ATTEMPTS &&
                    (exitCode == EXIT_CODE_TIMEOUT_LINUX || exitCode == EXIT_CODE_TIMEOUT_WINDOWS));

            handleProcessResult(resultStream, job);
        } catch (IOException e) {
            log.error("SteamCMD job ID {} failed due to an IO error", job.getId(), e);
            job.setState(JobStatus.FAILED);
            job.setErrorStatus(ErrorStatus.IO);
        } catch (Exception e) {
            log.error("SteamCMD job ID {} failed", job.getId(), e);
            job.setState(JobStatus.FAILED);
            job.setErrorStatus(ErrorStatus.GENERIC);
        }
    }

    private void handleProcessResult(InputStream cmdOutput, SteamCmdJob job) {
        // SteamCMD doesn't provide the user with any proper exit values or standard format for error messages.
        String errorLine = new BufferedReader(new InputStreamReader(cmdOutput)).lines()
                .map(String::toLowerCase)
                .map(this::removeParametersFromOutputLine)
                .filter(this::containsErrorKeyword)
                .findFirst()
                .orElse(null);

        if (errorLine == null) {
            job.setState(JobStatus.FINISHED);
            return;
        }

        job.setState(JobStatus.FAILED);
        job.setErrorStatus(ErrorStatus.GENERIC);
        if (errorLine.contains("login")) {
            job.setErrorStatus(ErrorStatus.WRONG_AUTH);
        }
        if (errorLine.contains("no subscription")) {
            job.setErrorStatus(ErrorStatus.NO_SUBSCRIPTION);
        }
        if (errorLine.contains("no match")) {
            job.setErrorStatus(ErrorStatus.NO_MATCH);
        }
        if (errorLine.contains("i/o operation")) {
            job.setErrorStatus(ErrorStatus.IO);
        }
    }

    private String getAuthString() {
        SteamAuth steamAuth = steamAuthRepository.findAll().stream().
                findFirst().orElseThrow();
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
