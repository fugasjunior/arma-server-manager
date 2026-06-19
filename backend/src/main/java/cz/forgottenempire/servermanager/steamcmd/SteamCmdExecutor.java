package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.steamauth.SteamSessionStatusHolder;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdOutputProcessor;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo.*;

@Service
@Slf4j
class SteamCmdExecutor {

    private static final List<String> ERROR_KEYWORDS = Arrays.asList("error", "failure", "failed");
    private static final int EXIT_CODE_TIMEOUT_LINUX = 134;
    private static final int EXIT_CODE_TIMEOUT_WINDOWS = 10;
    private static final int MAX_ATTEMPTS = 10;
    private static final String WORKSHOP_DOWNLOAD_TIMEOUT = "timeout downloading item";

    private static final String[] REAUTH_REQUIRED_ERRORS = {"account logon denied", "steam guard", "expired"};
    private static final String[] WRONG_AUTH_ERRORS = {"invalid password", "two-factor code mismatch"};

    private final PathsFactory pathsFactory;
    private final SteamSessionStatusHolder sessionStatusHolder;
    private final ProcessFactory processFactory;
    private final SteamCmdOutputProcessor steamCmdOutputProcessor;
    private final SteamCmdItemInfoRepository itemInfoRepository;

    @Autowired
    public SteamCmdExecutor(
            PathsFactory pathsFactory,
            SteamSessionStatusHolder sessionStatusHolder,
            ProcessFactory processFactory,
            SteamCmdOutputProcessor steamCmdOutputProcessor,
            SteamCmdItemInfoRepository itemInfoRepository
    ) {
        this.pathsFactory = pathsFactory;
        this.sessionStatusHolder = sessionStatusHolder;
        this.processFactory = processFactory;
        this.steamCmdOutputProcessor = steamCmdOutputProcessor;
        this.itemInfoRepository = itemInfoRepository;
    }

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    public boolean isBusy() {
        return executor.getActiveCount() > 0 || !executor.getQueue().isEmpty();
    }

    public void processJob(SteamCmdJob job, CompletableFuture<SteamCmdJob> future) {
        setItemInfoAsQueued(job);

        executor.submit(() -> {
            execute(job);
            future.complete(job);
        });
    }

    private void setItemInfoAsQueued(SteamCmdJob job) {
        ServerType relatedServerType = job.getRelatedServer();
        if (relatedServerType != null) {
            long serverAppId = Constants.SERVER_IDS.get(relatedServerType);
            itemInfoRepository.store(serverAppId,
                    new SteamCmdItemInfo(serverAppId, SteamCmdStatus.IN_QUEUE, 0, 0, 0));
        }

        Collection<WorkshopMod> relatedWorkshopMods = job.getRelatedWorkshopMods();
        if (relatedWorkshopMods != null) {
            relatedWorkshopMods.forEach(mod -> itemInfoRepository.store(mod.getId(),
                    new SteamCmdItemInfo(mod.getId(), SteamCmdStatus.IN_QUEUE, 0, 0, 0)));
        }
    }

    private void execute(SteamCmdJob job) {
        try {
            int attempts = 0;
            int exitCode;
            String output;

            do {
                attempts++;
                File steamCmdFile = pathsFactory.getSteamCmdExecutable();
                Process process = processFactory.startProcessWithUnbufferedOutput(steamCmdFile, job.getSteamCmdParameters().get());
                output = steamCmdOutputProcessor.processSteamCmdOutput(process.getInputStream(), job);
                exitCode = process.waitFor();
                if (attempts < MAX_ATTEMPTS && isRetryableTimeout(exitCode, output)) {
                    log.warn("SteamCMD download timed out, retrying job (attempt {}/{})", attempts + 1, MAX_ATTEMPTS);
                }
            } while (attempts < MAX_ATTEMPTS && isRetryableTimeout(exitCode, output));

            handleProcessResult(output, job);
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

    private boolean isRetryableTimeout(int exitCode, String output) {
        // Do not impose an ASM-side wall-clock timeout here. Large workshop items may download for
        // much longer than the SteamCMD timeout window, and they should continue as long as SteamCMD
        // itself is still running. Retry only after SteamCMD exits with a timeout signal/message.
        return exitedDueToTimeout(exitCode)
                || output.toLowerCase().contains(WORKSHOP_DOWNLOAD_TIMEOUT);
    }


    private void handleProcessResult(String result, SteamCmdJob job) {
        // SteamCMD doesn't provide proper exit values or a standard error format.
        String errorLine = result.lines()
                .map(String::toLowerCase)
                .map(this::removeParametersFromOutputLine)
                // Issue #69: missing steamservice.so / libSDL3.so.0 caused false positives
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

        if (Arrays.stream(REAUTH_REQUIRED_ERRORS).anyMatch(errorLine::contains)) {
            job.setErrorStatus(ErrorStatus.REAUTH_REQUIRED);
            sessionStatusHolder.setExpired();
        } else if (Arrays.stream(WRONG_AUTH_ERRORS).anyMatch(errorLine::contains)) {
            job.setErrorStatus(ErrorStatus.WRONG_AUTH);
        } else if (errorLine.contains("no subscription")) {
            job.setErrorStatus(ErrorStatus.NO_SUBSCRIPTION);
        } else if (errorLine.contains("no match")) {
            job.setErrorStatus(ErrorStatus.NO_MATCH);
        } else if (errorLine.contains("i/o operation") || errorLine.contains("failed to write file")) {
            job.setErrorStatus(ErrorStatus.IO);
        } else if (errorLine.contains("rate limit exceeded")) {
            job.setErrorStatus(ErrorStatus.RATE_LIMIT);
        }
        if (errorLine.contains(WORKSHOP_DOWNLOAD_TIMEOUT)) {
            job.setErrorStatus(ErrorStatus.TIMEOUT);
        }
    }

    private void dumpErrorOutputToLog(String result) {
        log.error(result);
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
