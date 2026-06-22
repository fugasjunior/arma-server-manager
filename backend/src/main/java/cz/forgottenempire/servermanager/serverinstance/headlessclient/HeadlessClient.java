package cz.forgottenempire.servermanager.serverinstance.headlessclient;

import com.google.common.base.Joiner;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.LogFile;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessCreator;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

@Slf4j
public class HeadlessClient {

    private final int id;
    private final Arma3Server server;
    private final PathsFactory pathsFactory;
    private final ServerProcessCreator serverProcessCreator;
    private final Path profilesPath;
    private final String[] additionalMods;
    private final int logMaxFiles;

    private Process process;

    public HeadlessClient(
            int id,
            Arma3Server server,
            PathsFactory pathsFactory,
            ServerProcessCreator serverProcessCreator,
            Path profilesPath,
            @Nullable String[] additionalMods,
            int logMaxFiles
    ) {
        this.id = id;
        this.server = server;
        this.pathsFactory = pathsFactory;
        this.serverProcessCreator = serverProcessCreator;
        this.profilesPath = profilesPath;
        this.additionalMods = additionalMods;
        this.logMaxFiles = logMaxFiles;
    }

    public HeadlessClient start() {
        File executable = pathsFactory.getServerExecutableWithFallback(ServerType.ARMA3);
        LogFile logFile = new LogFile(pathsFactory.getHeadlessClientLogFile(server.getId(), id));

        logFile.prepare(logMaxFiles);

        try {
            Files.createDirectories(profilesPath);
            List<String> parameters = prepareParameters();
            log.info("Starting headless client with options: {}", Joiner.on(" ").join(parameters));
            process = serverProcessCreator.startProcessWithRedirectedOutput(executable, parameters, logFile.getFile());
        } catch (IOException e) {
            log.error("Failed to start headless client", e);
        }

        return this;
    }

    public void stop() {
        if (!isAlive()) {
            return;
        }
        process.destroy();
    }

    public boolean isAlive() {
        return process != null && process.isAlive();
    }

    private List<String> prepareParameters() {
        List<String> parameters = new ArrayList<>();
        parameters.add("-client");
        parameters.add("-profiles=\"" + profilesPath + "\"");
        parameters.add("-connect=127.0.0.1:" + server.getPort());
        if (Strings.isNotBlank(server.getPassword())) {
            parameters.add("-password=" + server.getPassword());
        }

        List<String> modParameters = Stream.of(
                        server.getHeadlessClientModsAsParameters(),
                        server.getCreatorDlcsAsParameters(),
                        server.getAdditionalModsAsParameters(additionalMods)
                )
                .flatMap(Function.identity())
                .toList();

        parameters.addAll(modParameters);
        return parameters;
    }
}
