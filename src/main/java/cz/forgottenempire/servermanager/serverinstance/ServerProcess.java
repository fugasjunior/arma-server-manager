package cz.forgottenempire.servermanager.serverinstance;

import com.google.common.base.Joiner;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configurable
public class ServerProcess {

    private final long serverId;
    private PathsFactory pathsFactory;
    private ServerRepository serverRepository;
    private Process process;
    private ServerInstanceInfo instanceInfo;

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
            process = startProcessWithRedirectedOutput(executable, parameters, server.getLog().getFile());
            log.info("Server '{}' (ID {}) started (PID {})", server.getName(), server.getId(), process.pid());
        } catch (IOException e) {
            log.error("Could not start server '{}' (ID {})", server.getName(), server.getId(), e);
        }

        instanceInfo = ServerInstanceInfo.builder()
                .startedAt(LocalDateTime.now())
                .maxPlayers(server.getMaxPlayers())
                .build();
        return process;
    }

    public void stop() {
        if (isAlive()) {
            process.destroy();
        }

        instanceInfo = ServerInstanceInfo.builder().build();
    }

    public boolean isAlive() {
        return process != null && process.isAlive();
    }

    private Process startProcessWithRedirectedOutput(File executable, List<String> parameters, File outputFile)
            throws IOException {
        File directory = executable.getParentFile();
        List<String> commands = new ArrayList<>();
        commands.add(executable.getAbsolutePath());
        commands.addAll(parameters);

        return new ProcessBuilder(commands)
                .directory(directory)
                .redirectErrorStream(true)
                .redirectOutput(ProcessBuilder.Redirect.appendTo(outputFile))
                .start();
    }

    @Autowired
    void setPathsFactory(PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
    }

    @Autowired
    void setServerRepository(ServerRepository serverRepository) {
        this.serverRepository = serverRepository;
    }
}
