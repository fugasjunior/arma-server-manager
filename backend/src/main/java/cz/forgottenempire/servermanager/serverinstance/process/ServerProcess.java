package cz.forgottenempire.servermanager.serverinstance.process;

import com.google.common.base.Joiner;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.AutomaticRestartTask;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.serverinstance.ServerInstanceInfo;
import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;

import java.io.File;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
public class ServerProcess {

    private final long serverId;
    protected final ServerProcessCreator serverProcessCreator;
    protected final PathsFactory pathsFactory;
    protected final ServerRepository serverRepository;
    private final Clock clock;
    private final TaskScheduler taskScheduler;
    private Process process;
    private AutomaticRestartTask automaticRestartTask;
    protected ServerInstanceInfo instanceInfo;

    public ServerProcess(long serverId, ServerProcessCreator serverProcessCreator, PathsFactory pathsFactory,
                         ServerRepository serverRepository, Clock clock, TaskScheduler taskScheduler) {
        this.serverId = serverId;
        this.serverProcessCreator = serverProcessCreator;
        this.pathsFactory = pathsFactory;
        this.serverRepository = serverRepository;
        this.clock = clock;
        this.taskScheduler = taskScheduler;
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
        log.info("Stopping server ID {}", serverId);

        if (isAlive()) {
            process.destroy();
            process = null;
        }

        cancelRestartJob();
        instanceInfo = ServerInstanceInfo.builder().build();
    }

    public void restart() {
        log.info("Restarting server ID {}", serverId);

        stop();
        start();
    }

    public boolean isAlive() {
        return process != null && process.isAlive();
    }

    public void scheduleRestartJobAt(LocalTime time) {
        if (automaticRestartTask != null) {
            automaticRestartTask.cancel();
        }
        automaticRestartTask = new AutomaticRestartTask(this, time, clock, taskScheduler).schedule();
    }

    public void cancelRestartJob() {
        if (automaticRestartTask != null) {
            automaticRestartTask.cancel();
        }
    }
}
