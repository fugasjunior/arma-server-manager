package cz.forgottenempire.servermanager.serverinstance;

import com.google.common.base.Joiner;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ServerProcess {

    private final PathsFactory pathsFactory;
    private final Server server;
    private Process process;

    public ServerProcess(Server server, PathsFactory pathsFactory) {
        this.pathsFactory = pathsFactory;
        this.server = server;
    }

    public Process start() {
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
        return process;
    }

    public void stop() {
        process.destroy();
    }

    public Process startProcessWithRedirectedOutput(File executable, List<String> parameters, File outputFile)
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
