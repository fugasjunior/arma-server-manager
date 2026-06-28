package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.steamauth.SteamSessionStatusHolder;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdOutputProcessor;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SteamCmdExecutorTest {

    private static final String TIMEOUT_OUTPUT =
            "Downloading item 450814997 ...ERROR! Timeout downloading item 450814997";

    @Test
    void retriesWorkshopDownloadTimeoutEvenWhenExitCodeIsNotTimeout() throws Exception {
        TestContext context = new TestContext();
        when(context.outputProcessor.processSteamCmdOutput(any(), any()))
                .thenReturn(TIMEOUT_OUTPUT)
                .thenReturn("Success. Downloaded item 450814997");

        SteamCmdJob result = context.execute();

        assertThat(result.getErrorStatus()).isNull();
        verify(context.processFactory, times(2))
                .startProcessWithUnbufferedOutput(any(File.class), anyList());
    }

    @Test
    void reportsTimeoutAfterRetryLimitIsExhausted() throws Exception {
        TestContext context = new TestContext();
        when(context.outputProcessor.processSteamCmdOutput(any(), any()))
                .thenReturn(TIMEOUT_OUTPUT);

        SteamCmdJob result = context.execute();

        assertThat(result.getErrorStatus()).isEqualTo(ErrorStatus.TIMEOUT);
        verify(context.processFactory, times(10))
                .startProcessWithUnbufferedOutput(any(File.class), anyList());
    }
    @Test
    void reportsMissingCachedCredentialsAsReauthRequired() throws Exception {
        TestContext context = new TestContext();
        when(context.outputProcessor.processSteamCmdOutput(any(), any()))
                .thenReturn("FAILED (No cached credentials and @NoPromptForPassword is set)\nERROR! Not logged on.");

        SteamCmdJob result = context.execute();

        assertThat(result.getErrorStatus()).isEqualTo(ErrorStatus.REAUTH_REQUIRED);
        verify(context.sessionStatusHolder).setExpired();
    }


    @Test
    void doesNotRetrySuccessfulLongRunningDownloadOutput() throws Exception {
        TestContext context = new TestContext();
        when(context.outputProcessor.processSteamCmdOutput(any(), any()))
                .thenReturn("Downloading 6285 chunks for depot 107410. Success. Downloaded item 3549838320");

        SteamCmdJob result = context.execute();

        assertThat(result.getErrorStatus()).isNull();
        verify(context.processFactory, times(1))
                .startProcessWithUnbufferedOutput(any(File.class), anyList());
    }

    private static class TestContext {
        private final PathsFactory pathsFactory = mock(PathsFactory.class);
        private final SteamSessionStatusHolder sessionStatusHolder = mock(SteamSessionStatusHolder.class);
        private final ProcessFactory processFactory = mock(ProcessFactory.class);
        private final SteamCmdOutputProcessor outputProcessor = mock(SteamCmdOutputProcessor.class);
        private final SteamCmdItemInfoRepository itemInfoRepository = new SteamCmdItemInfoRepository();
        private final SteamCmdExecutor executor = new SteamCmdExecutor(
                pathsFactory,
                sessionStatusHolder,
                processFactory,
                outputProcessor,
                itemInfoRepository
        );

        private TestContext() throws Exception {
            File executable = new File("/tmp/steamcmd");
            Process process = mock(Process.class);
            when(pathsFactory.getSteamCmdExecutable()).thenReturn(executable);
            when(process.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));
            when(process.waitFor()).thenReturn(0);
            when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList()))
                    .thenReturn(process);
        }

        private SteamCmdJob execute() throws Exception {
            SteamCmdParameters parameters = new SteamCmdParameters.Builder()
                    .withCachedLogin("test")
                    .build();
            SteamCmdJob job = new SteamCmdJob(List.of(), parameters);
            CompletableFuture<SteamCmdJob> future = new CompletableFuture<>();

            executor.processJob(job, future);

            return future.get(5, TimeUnit.SECONDS);
        }
    }
}
