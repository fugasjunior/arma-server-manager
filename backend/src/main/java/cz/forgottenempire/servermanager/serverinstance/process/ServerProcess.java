package cz.forgottenempire.servermanager.serverinstance.process;

import com.google.common.base.Joiner;
import cz.forgottenempire.servermanager.common.ServerStatus;
import cz.forgottenempire.servermanager.serverinstance.ServerConfig;
import cz.forgottenempire.servermanager.serverinstance.ServerInstanceInfo;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
public class ServerProcess {

    private final long serverId;
    protected final ServerProcessCreator serverProcessCreator;
    protected final ServerLaunchContext launchContext;
    private final int logMaxFiles;
    private Process process;
    protected ServerInstanceInfo instanceInfo;

    public ServerProcess(long serverId, ServerProcessCreator serverProcessCreator, ServerLaunchContext launchContext, int logMaxFiles) {
        this.serverId = serverId;
        this.serverProcessCreator = serverProcessCreator;
        this.launchContext = launchContext;
        this.logMaxFiles = logMaxFiles;
    }

    public ServerInstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public long getServerId() {
        return serverId;
    }

    public Process start(Server server) {
        if (isAlive()) {
            return process;
        }

        var executable = launchContext.pathsFactory().getServerExecutableWithFallback(server.getType());
        List<String> parameters = server.getLaunchParameters(launchContext);

        try {
            server.prepareLaunchEnvironment(launchContext);
        } catch (IOException e) {
            log.error("Could not prepare launch environment for server ID {}", server.getId(), e);
            instanceInfo = ServerInstanceInfo.builder().status(ServerStatus.ERROR).build();
            return null;
        }

        server.getConfigFiles(launchContext).forEach(ServerConfig::generateIfNecessary);
        var logFile = server.getLog(launchContext.pathsFactory());
        logFile.prepare(logMaxFiles);

        try {
            log.info("Starting server with options: {}", Joiner.on(" ").join(parameters));
            process = serverProcessCreator.startProcessWithRedirectedOutput(
                    executable, parameters, logFile.getFile());
            log.info("Server '{}' (ID {}) started (PID {})", server.getName(), server.getId(), process.pid());
        } catch (IOException e) {
            log.error("Could not start server '{}' (ID {})", server.getName(), server.getId(), e);
            instanceInfo = ServerInstanceInfo.builder().status(ServerStatus.ERROR).build();
            return null;
        }

        instanceInfo = ServerInstanceInfo.builder()
                .status(ServerStatus.STARTING)
                .startedAt(LocalDateTime.now())
                .maxPlayers(server.getMaxPlayers())
                .build();

        return process;
    }

    public void markCrashed() {
        process = null;
        instanceInfo = ServerInstanceInfo.builder().status(ServerStatus.ERROR).build();
    }

    public void stop() {
        log.info("Stopping server ID {}", serverId);

        if (isAlive()) {
            process.destroy();
            process = null;
        }

        instanceInfo = ServerInstanceInfo.builder().build();
    }

    public void restart(Server server) {
        log.info("Restarting server ID {}", serverId);
        stop();
        start(server);
    }

    public boolean isAlive() {
        return process != null && process.isAlive();
    }
}
