package cz.forgottenempire.servermanager.serverinstance;

import com.google.common.base.Joiner;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Configurable
public class ServerProcess {

    private final long serverId;
    private ServerProcessCreator serverProcessCreator;
    private PathsFactory pathsFactory;
    private ServerRepository serverRepository;
    private Process process;
    private ServerInstanceInfo instanceInfo;
    private AutomaticRestartTask automaticRestartTask;

    public ServerProcess(long serverId) {
        this.serverId = serverId;
    }

    public ServerInstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public long getServerId() {
        return serverId;
    }

    public Process start() {
        if (isAlive()) {
            return process;
        }

        Server server = serverRepository.findById(serverId).orElseThrow();

        File executable = pathsFactory.getServerExecutableWithFallback(server.getType());
        List<String> parameters = server.getLaunchParameters();

        server.getConfigFiles().forEach(ServerConfig::generateIfNecessary);
        server.getLog().prepare();

        try {
            log.info("Starting server with options: {}", Joiner.on(" ").join(parameters));
            process = serverProcessCreator.startProcessWithRedirectedOutput(executable, parameters, server.getLog().getFile());
            log.info("Server '{}' (ID {}) started (PID {})", server.getName(), server.getId(), process.pid());
        } catch (IOException e) {
            log.error("Could not start server '{}' (ID {})", server.getName(), server.getId(), e);
            return null;
        }

        instanceInfo = ServerInstanceInfo.builder()
                .startedAt(LocalDateTime.now())
                .maxPlayers(server.getMaxPlayers())
                .build();

        if (server.isRestartAutomatically()) {
            scheduleRestartJobAt(server.getAutomaticRestartTime());
        }

        return process;
    }

    public void stop() {
        if (isAlive()) {
            process.destroy();
        }

        cancelRestartJob();
        instanceInfo = ServerInstanceInfo.builder().build();
    }

    public void restart() {
        stop();
        start();
    }

    public boolean isAlive() {
        return process != null && process.isAlive();
    }

    public void scheduleRestartJobAt(LocalTime time) {
        automaticRestartTask = new AutomaticRestartTask(this, time).schedule();
    }

    private void cancelRestartJob() {
        if (automaticRestartTask != null) {
            automaticRestartTask.cancel();
        }
    }

    @Autowired
    void setPathsFactory(PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
    }

    @Autowired
    void setServerRepository(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }

    @Autowired
    void setServerProcessCreator(ServerProcessCreator serverProcessCreator) {
        this.serverProcessCreator = serverProcessCreator;
    }
}
