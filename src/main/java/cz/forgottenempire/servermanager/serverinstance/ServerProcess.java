package cz.forgottenempire.servermanager.serverinstance;

import com.google.common.base.Joiner;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerProcess {

    private final PathsFactory pathsFactory;
    private final Server server;
    private Process process;
    private ServerInstanceInfo instanceInfo;

    public ServerProcess(Server server, PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
        this.server = server;
    }

    public ServerInstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public long getServerId() {
        return server.getId();
    }

    public Process start() {
        if (isAlive()) {
            return process;
        }

        File executable = pathsFactory.getServerExecutableWithFallback(server.getType());
        List<String> parameters = server.getLaunchParameters();

        server.getLog().prepare();

        try {
            log.info("Starting server with options: {}", Joiner.on(" ").join(parameters));
            process = startProcessWithRedirectedOutput(executable, parameters, server.getLog().getFile());
            log.info("Server '{}' (ID {}) started (PID {})", server.getName(), server.getId(), process.pid());
        } catch (IOException e) {
            log.error("Could not start server '{}' (ID {})", server.getName(), server.getId(), e);
        }

        instanceInfo = ServerInstanceInfo.builder()
                .id(server.getId())
                .startedAt(LocalDateTime.now())
                .maxPlayers(server.getMaxPlayers())
                .process(process)
                .build();
        return process;
    }

    public void stop() {
        if (isAlive()) {
            process.destroy();
        }

        instanceInfo = ServerInstanceInfo.builder()
                .id(server.getId())
                .build();
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
}
