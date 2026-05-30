package cz.forgottenempire.servermanager.support.fakes;

import cz.forgottenempire.servermanager.common.ProcessFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Primary
public class FakeProcessFactory extends ProcessFactory {

    private final Map<String, Deque<FakeProcess>> scripts = new ConcurrentHashMap<>();
    private final Deque<FakeProcess> serverProcesses = new ArrayDeque<>();
    private volatile FakeProcess lastSteamCmdProcess;

    public void scriptSteamCmd(FakeProcess process) {
        scripts.computeIfAbsent("steamcmd", k -> new ArrayDeque<>()).addLast(process);
    }

    public void terminateCurrentSteamCmd() {
        FakeProcess p = lastSteamCmdProcess;
        if (p != null) {
            p.terminate();
        }
    }

    public void scriptSteamCmdWithFixture(String fixtureName) {
        try {
            byte[] content = FakeProcessFactory.class.getClassLoader()
                    .getResourceAsStream("steamcmd-output/" + fixtureName)
                    .readAllBytes();
            scriptSteamCmd(FakeProcess.withOutput(new String(content), 0));
        } catch (IOException e) {
            throw new RuntimeException("Could not load fixture: " + fixtureName, e);
        }
    }

    public void scriptServerProcess(FakeProcess process) {
        synchronized (serverProcesses) {
            serverProcesses.addLast(process);
        }
    }

    public void reset() {
        scripts.clear();
        synchronized (serverProcesses) {
            serverProcesses.clear();
        }
    }

    @Override
    public Process startProcessWithUnbufferedOutput(File executable, List<String> parameters) throws IOException {
        return resolveProcess(executable);
    }

    @Override
    public Process startProcess(File executable, List<String> parameters) throws IOException {
        return resolveProcess(executable);
    }

    @Override
    public Process startProcess(File executable, List<String> parameters, File directory) throws IOException {
        return resolveProcess(executable);
    }

    @Override
    public Process startProcessWithDiscardedOutput(File executable, List<String> parameters) throws IOException {
        return resolveProcess(executable);
    }

    @Override
    public Process startProcessWithRedirectedOutput(File executable, List<String> parameters, File outputFile)
            throws IOException {
        ensureParentDirExists(outputFile);
        return resolveProcess(executable);
    }

    @Override
    public Process startProcessWithRedirectedOutput(File executable, List<String> parameters, File directory,
            File outputFile) throws IOException {
        ensureParentDirExists(outputFile);
        return resolveProcess(executable);
    }

    public Process resolveForServerProcess() {
        synchronized (serverProcesses) {
            FakeProcess scripted = serverProcesses.pollFirst();
            if (scripted != null) {
                return scripted;
            }
        }
        return FakeProcess.stayingAlive();
    }

    private Process resolveProcess(File executable) {
        String name = executable != null ? executable.getName().toLowerCase() : "";
        if (name.contains("steamcmd")) {
            Deque<FakeProcess> queue = scripts.get("steamcmd");
            if (queue != null) {
                FakeProcess p = queue.pollFirst();
                if (p != null) {
                    lastSteamCmdProcess = p;
                    return p;
                }
            }
            FakeProcess p = FakeProcess.exiting(0);
            lastSteamCmdProcess = p;
            return p;
        }
        return resolveForServerProcess();
    }

    private void ensureParentDirExists(File outputFile) {
        if (outputFile != null && outputFile.getParentFile() != null) {
            outputFile.getParentFile().mkdirs();
        }
    }
}
