package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.Arma3KeyService;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.LogFile;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class Arma3ServerProcessTest {

    @TempDir
    private File tempDir;

    private static final long SERVER_ID = 1L;

    private Arma3ServerProcess arma3ServerProcess;
    private Arma3Server arma3Server;
    private ServerProcessCreator processCreator;
    private Process process;

    @BeforeEach
    void setUp() throws IOException {
        PathsFactory pathsFactory = mock(PathsFactory.class, withSettings().stubOnly());
        File executable = mock(File.class, withSettings().stubOnly());
        when(pathsFactory.getServerExecutableWithFallback(ServerType.ARMA3)).thenReturn(executable);

        when(pathsFactory.getHeadlessClientLogFile(anyLong(), anyInt())).thenAnswer(invocation -> {
            long serverId = invocation.getArgument(0);
            int hcId = invocation.getArgument(1);
            File hcLogFile = new File(tempDir, "ARMA3_" + serverId + "_HC " + hcId + ".log");
            hcLogFile.getParentFile().mkdirs();
            return hcLogFile;
        });

        LogFile logFile = mock(LogFile.class);
        when(logFile.getFile()).thenReturn(mock(File.class, withSettings().stubOnly()));

        arma3Server = mock(Arma3Server.class, withSettings().stubOnly());
        when(arma3Server.getId()).thenReturn(SERVER_ID);
        when(arma3Server.getType()).thenReturn(ServerType.ARMA3);
        when(arma3Server.getLog(pathsFactory)).thenReturn(logFile);

        processCreator = mock(ServerProcessCreator.class);
        process = mock(Process.class, withSettings().stubOnly());
        when(processCreator.startProcessWithRedirectedOutput(any(), any(), any())).thenReturn(process);

        Arma3InstancePaths instancePaths = mock(Arma3InstancePaths.class, withSettings().stubOnly());
        when(instancePaths.getHeadlessClientProfilesPath(anyLong(), anyInt())).thenAnswer(invocation -> {
            long serverId = invocation.getArgument(0);
            int hcId = invocation.getArgument(1);
            return Path.of(tempDir.getAbsolutePath(), "profiles", "ARMA3_" + serverId, "hc", String.format("hc_%02d", hcId));
        });

        ServerLaunchContext launchContext = new ServerLaunchContext(
                pathsFactory,
                instancePaths,
                mock(Arma3KeyService.class, withSettings().stubOnly()),
                mock(FreeMarkerConfigurer.class, withSettings().stubOnly()));

        arma3ServerProcess = new Arma3ServerProcess(SERVER_ID, processCreator, launchContext, new String[0], 5);
    }

    @Test
    void reconcileHeadlessClients_whenServerProcessNotAlive_doesNotSpawnHeadlessClients() throws IOException {
        when(arma3Server.getTargetHeadlessClientsCount()).thenReturn(2);
        arma3ServerProcess.start(arma3Server);
        when(process.isAlive()).thenReturn(false);

        arma3ServerProcess.reconcileHeadlessClients(arma3Server);

        // Only the initial server start — no HC processes spawned
        verify(processCreator, times(1)).startProcessWithRedirectedOutput(any(), any(), any());
        assertThat(arma3ServerProcess.getInstanceInfo().getHeadlessClientsCount()).isZero();
    }

    @Test
    void reconcileHeadlessClients_spawnsCorrectNumberOfHCs() throws IOException {
        when(process.isAlive()).thenReturn(true);
        when(arma3Server.getTargetHeadlessClientsCount()).thenReturn(3);
        arma3ServerProcess.start(arma3Server);

        arma3ServerProcess.reconcileHeadlessClients(arma3Server);

        // 1 server start + 3 HC starts
        verify(processCreator, times(4)).startProcessWithRedirectedOutput(any(), any(), any());
        assertThat(arma3ServerProcess.getInstanceInfo().getHeadlessClientsCount()).isEqualTo(3);
    }

    @Test
    void reconcileHeadlessClients_decreasedTarget_stopsExcessHCs() throws IOException {
        when(process.isAlive()).thenReturn(true);
        when(arma3Server.getTargetHeadlessClientsCount()).thenReturn(3);
        arma3ServerProcess.start(arma3Server);
        arma3ServerProcess.reconcileHeadlessClients(arma3Server);

        when(arma3Server.getTargetHeadlessClientsCount()).thenReturn(1);
        arma3ServerProcess.reconcileHeadlessClients(arma3Server);

        assertThat(arma3ServerProcess.getInstanceInfo().getHeadlessClientsCount()).isOne();
    }

    @Test
    void reconcileHeadlessClients_prunesDeadHC_andSpawnsReplacement() throws IOException {
        Process serverProcess = mock(Process.class);
        when(serverProcess.isAlive()).thenReturn(true);
        Process deadHcProcess = mock(Process.class);
        when(deadHcProcess.isAlive()).thenReturn(false);
        Process replacementHcProcess = mock(Process.class);
        when(replacementHcProcess.isAlive()).thenReturn(true);
        when(processCreator.startProcessWithRedirectedOutput(any(), any(), any()))
                .thenReturn(serverProcess)
                .thenReturn(deadHcProcess)
                .thenReturn(replacementHcProcess);

        when(arma3Server.getTargetHeadlessClientsCount()).thenReturn(1);
        arma3ServerProcess.start(arma3Server);
        arma3ServerProcess.reconcileHeadlessClients(arma3Server); // spawns deadHcProcess

        arma3ServerProcess.reconcileHeadlessClients(arma3Server); // prunes dead, spawns replacementHcProcess

        assertThat(arma3ServerProcess.getInstanceInfo().getHeadlessClientsCount()).isOne();
        verify(processCreator, times(3)).startProcessWithRedirectedOutput(any(), any(), any());
    }

    @Test
    void restart_restartsServerAndRespawnsHCsToTarget() throws IOException {
        when(process.isAlive()).thenReturn(true);
        when(arma3Server.getTargetHeadlessClientsCount()).thenReturn(2);
        arma3ServerProcess.start(arma3Server);

        arma3ServerProcess.restart(arma3Server);

        // 1 initial start + 1 restart start + 2 HCs spawned on restart
        verify(processCreator, times(4)).startProcessWithRedirectedOutput(any(), any(), any());
        assertThat(arma3ServerProcess.getInstanceInfo().getHeadlessClientsCount()).isEqualTo(2);
    }
}
