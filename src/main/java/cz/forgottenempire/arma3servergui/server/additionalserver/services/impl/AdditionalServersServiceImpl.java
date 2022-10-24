package cz.forgottenempire.arma3servergui.server.additionalserver.services.impl;

import cz.forgottenempire.arma3servergui.server.additionalserver.AdditionalServerInstanceInfo;
import cz.forgottenempire.arma3servergui.server.additionalserver.entities.AdditionalServer;
import cz.forgottenempire.arma3servergui.server.additionalserver.repositories.AdditionalServerInstanceInfoRepository;
import cz.forgottenempire.arma3servergui.server.additionalserver.repositories.AdditionalServerRepository;
import cz.forgottenempire.arma3servergui.server.additionalserver.services.AdditionalServersService;
import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdditionalServersServiceImpl implements AdditionalServersService {

    private final AdditionalServerRepository serverRepository;

    private final AdditionalServerInstanceInfoRepository instanceInfoRepository;

    @Value("${arma3server.logDir}")
    private String logDirectory;

    @Autowired
    public AdditionalServersServiceImpl(
            AdditionalServerRepository serverRepository,
            AdditionalServerInstanceInfoRepository instanceInfoRepository
    ) {
        this.serverRepository = serverRepository;
        this.instanceInfoRepository = instanceInfoRepository;

        // add shutdown hook to stop all running servers
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                instanceInfoRepository.getAll().forEach(server -> destroyWithTimeout(server.getProcess()))));
    }


    public Optional<AdditionalServer> getServer(Long id) {
        return serverRepository.findById(id);
    }

    public AdditionalServerInstanceInfo getServerInstanceInfo(Long id) {
        return instanceInfoRepository.getServerInstanceInfo(id);
    }

    @Override
    public List<AdditionalServer> getAllServers() {
        return serverRepository.findAll();
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
            instanceInfoRepository.storeServerInstanceInfo(serverId,
                    new AdditionalServerInstanceInfo(serverId, true, LocalDateTime.now(), process));
            log.info("Server '{}' started (PID {})", settings.getName(), process.pid());
        } catch (IOException e) {
            log.error("Could not start server {} with command {} in directory {} due to {}",
                    settings.getName(), settings.getCommand(), settings.getServerDir(), e.getMessage());
        }
    }

    @Override
    public void stopServer(Long serverId) {
        AdditionalServerInstanceInfo instanceInfo = instanceInfoRepository.getServerInstanceInfo(serverId);
        Process process = instanceInfo.getProcess();
        if (process == null) {
            log.warn("Server ID {} could not be stopped because it's not running", serverId);
            return;
        }

        destroyWithTimeout(process);
        instanceInfoRepository.storeServerInstanceInfo(serverId,
                new AdditionalServerInstanceInfo(serverId, false, null, null));
        log.info("Server id {} stopped", serverId);
    }

    private boolean isAlive(Long serverId) {
        AdditionalServerInstanceInfo instanceInfo = instanceInfoRepository.getServerInstanceInfo(serverId);
        return instanceInfo.isAlive();
    }

    // TODO split log files automatically
    private File getLogFile(String serverName) {
        File logFile = new File(Path.of(logDirectory, sanitizeServerName(serverName), "log.txt").toUri());
        File parent = logFile.getParentFile();
        if (!parent.exists() && !parent.mkdirs()) {
            log.error("Failed to create log directory '{}'", parent);
            throw new IllegalStateException("Couldn't create dir: " + parent);
        }
        return logFile;
    }

    private String sanitizeServerName(String serverName) {
        return serverName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
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
}