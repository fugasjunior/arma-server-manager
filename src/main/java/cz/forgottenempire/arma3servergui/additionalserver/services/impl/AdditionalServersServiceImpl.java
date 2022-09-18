package cz.forgottenempire.arma3servergui.additionalserver.services.impl;

import com.google.common.collect.Lists;
import cz.forgottenempire.arma3servergui.additionalserver.entities.AdditionalServer;
import cz.forgottenempire.arma3servergui.additionalserver.repositories.AdditionalServerRepository;
import cz.forgottenempire.arma3servergui.additionalserver.services.AdditionalServersService;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdditionalServersServiceImpl implements AdditionalServersService {

    private final Map<Long, Process> serverProcesses = new ConcurrentHashMap<>();
    private AdditionalServerRepository serverRepository;

    @Value("${arma3server.logDir}")
    private String logDirectory;

    public AdditionalServersServiceImpl() {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                serverProcesses.forEach((id, process) -> destroyWithTimeout(process))));
    }

    public Optional<AdditionalServer> getServer(Long id) {
        return serverRepository.findById(id);
    }

    @Override
    public void startServer(Long serverId) {
        if (isAlive(serverId)) {
            log.info("Server id {} already running", serverId);
            return;
        }

        AdditionalServer settings = serverRepository
                .findById(serverId)
                .orElseThrow(NoSuchElementException::new);

        List<String> commands = Arrays.asList(settings.getCommand().split(" "));

        try {
            Process process = new ProcessBuilder()
                    .directory(new File(settings.getServerDir()))
                    .command(commands)
                    .redirectOutput(Redirect.appendTo(getLogFile(settings.getName())))
                    .redirectError(Redirect.appendTo(getLogFile(settings.getName())))
                    .start();
            serverProcesses.put(serverId, process);
            log.info("{} server started (PID {})", settings.getName(), process.pid());
        } catch (IOException e) {
            log.error("Could not start server {} with command {} in directory {} due to {}",
                    settings.getName(), settings.getCommand(), settings.getServerDir(), e.getMessage());
        }
    }

    private File getLogFile(String serverName) {
        File logFile = new File(Path.of(logDirectory, sanitizeServerName(serverName), "log.txt").toUri());
        File parent = logFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        return logFile;
    }

    private String sanitizeServerName(String serverName) {
        return serverName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
    }

    @Override
    public void stopServer(Long serverId) {
        if (!serverProcesses.containsKey(serverId)) {
            return;
        }
        Process process = serverProcesses.get(serverId);
        destroyWithTimeout(process);
        serverProcesses.remove(serverId);
        log.info("Server id {} stopped", serverId);
    }

    private void destroyWithTimeout(Process process) {
        process.descendants().forEach(ProcessHandle::destroy);
        process.destroy();

        if (process.isAlive()) {
            try {
                Thread.sleep(30 * 1000L);
            } catch (InterruptedException e) {
                log.error("Thread interrupted");
                Thread.currentThread().interrupt();
            } finally {
                if (process.isAlive()) {
                    process.descendants().forEach(ProcessHandle::destroyForcibly);
                    process.destroyForcibly();
                }
            }
        }
    }

    @Override
    public boolean isAlive(Long serverId) {
        if (!serverProcesses.containsKey(serverId)) {
            return false;
        }
        return serverProcesses.get(serverId).isAlive();
    }

    @Override
    public Collection<AdditionalServer> getAllServers() {
        return Lists.newArrayList(serverRepository.findAll());
    }

    @Autowired
    public void setServerRepository(
            AdditionalServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }
}
