package cz.forgottenempire.arma3servergui.services.impl;

import com.google.common.collect.Lists;
import cz.forgottenempire.arma3servergui.model.AdditionalServer;
import cz.forgottenempire.arma3servergui.repositories.AdditionalServerRepository;
import cz.forgottenempire.arma3servergui.services.AdditionalServersService;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdditionalServersServiceImpl implements AdditionalServersService {

    private final Map<Long, Process> serverProcesses = new HashMap<>();
    private AdditionalServerRepository serverRepository;

    public AdditionalServersServiceImpl() {
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                serverProcesses.forEach((id, process) -> destroyWithTimeout(process))));
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
                    .start();
            serverProcesses.put(serverId, process);
            log.info("{} server started (PID {})", settings.getName(), process.pid());
        } catch (IOException e) {
            log.error("Could not start server {} with command {} in directory {} due to {}",
                    settings.getName(), settings.getCommand(), settings.getServerDir(), e.getMessage());
        }
    }

    @Override
    public void stopServer(Long serverId) {
        if (!serverProcesses.containsKey(serverId)) return;
        Process process = serverProcesses.get(serverId);
        destroyWithTimeout(process);
        serverProcesses.remove(serverId);
        log.info("Server id {} stopped", serverId);
    }

    private void destroyWithTimeout(Process process) {
        process.destroy();

        if (process.isAlive()) {
            try {
                Thread.sleep(10 * 1000L);
            } catch (InterruptedException ignored) {} finally {
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
            }
        }
    }

    @Override
    public boolean isAlive(Long serverId) {
        if (!serverProcesses.containsKey(serverId)) return false;
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
