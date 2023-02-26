package cz.forgottenempire.servermanager.common;

import cz.forgottenempire.servermanager.common.exceptions.ServerNotInitializedException;
import cz.forgottenempire.servermanager.util.SystemUtils;
import cz.forgottenempire.servermanager.util.SystemUtils.OSType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

@Component
public class PathsFactory {

    private final Path modsBasePath;
    private final Path serversBasePath;
    private final Path logsBasePath;

    @Autowired
    public PathsFactory(
            @Value("${directory.servers}") String serversPathString,
            @Value("${directory.mods}") String modsPathString,
            @Value("${directory.logs}") String logsPathString
    ) {
        serversBasePath = Path.of(serversPathString);
        modsBasePath = Path.of(modsPathString);
        logsBasePath = Path.of(logsPathString);
    }

    public Path getModsBasePath() {
        return modsBasePath;
    }

    public Path getServersBasePath() {
        return serversBasePath;
    }

    public Path getServerPath(ServerType type) {
        return Path.of(getServersBasePath().toString(), type.name());
    }

    public Path getModsPath(ServerType type) {
        return Path.of(getModsBasePath().toString(), "steamapps", "workshop", "content",
                String.valueOf(Constants.GAME_IDS.get(type)));
    }

    public Path getModInstallationPath(Long modId, ServerType type) {
        return Path.of(getModsPath(type).toString(), String.valueOf(modId));
    }

    public Path getModLinkPath(String modName, ServerType type) {
        return Path.of(getServerPath(type).toString(), modName);
    }

    public Path getServerKeysPath(ServerType type) {
        return Path.of(getServerPath(type).toString(), "keys");
    }

    public Path getServerKeyPath(String keyName, ServerType type) {
        return Path.of(getServerKeysPath(type).toString(), keyName);
    }

    public Path getScenariosBasePath() {
        return Path.of(getServerPath(ServerType.ARMA3).toString(), "mpmissions");
    }

    public Path getScenarioPath(String scenarioName) {
        return Path.of(getScenariosBasePath().toString(), scenarioName);
    }

    public Path getConfigFilePath(ServerType type, String configName) {
        return Path.of(getServerPath(type).toString(), configName).toAbsolutePath();
    }

    public File getServerExecutableWithFallback(ServerType type) {
        File executable;
        if (SystemUtils.getOsType() == OSType.WINDOWS) {
            executable = new File(getServerExecutable(type).toAbsolutePath() + ".exe");
        } else {
            executable = getServerExecutable(type).toFile();
        }

        if (executable.isFile()) {
            return executable;
        }

        // no x64 executable found, try fallback
        String pathString = executable.toString();
        // remove '_x64' suffix
        pathString = getServerExecutable(type).toAbsolutePath().toString().substring(0, pathString.length() - 4);
        if (SystemUtils.getOsType() == OSType.WINDOWS) {
            pathString += ".exe";
        }
        executable = new File(pathString);
        if (executable.isFile()) {
            return executable;
        }

        throw new ServerNotInitializedException("Couldn't find any server executable for " + type + " server");
    }

    public File getServerLogFile(ServerType type, long id) {
        return Path.of(logsBasePath.toString(), type.name() + "_" + id).toFile();
    }

    private Path getServerExecutable(ServerType type) {
        return Path.of(getServerPath(type).toString(), Constants.SERVER_EXECUTABLES.get(type));
    }
}
