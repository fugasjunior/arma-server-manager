package cz.forgottenempire.arma3servergui.common;

import cz.forgottenempire.arma3servergui.util.SystemUtils;
import cz.forgottenempire.arma3servergui.util.SystemUtils.OSType;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PathsFactory {

    private final Path modsBasePath;
    private final Path serversBasePath;

    @Autowired
    public PathsFactory(
            @Value("${directory.servers}") String serversPathString,
            @Value("${directory.mods}") String modsPathString
    ) {
        serversBasePath = Path.of(serversPathString);
        modsBasePath = Path.of(modsPathString);
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

    public File getConfigFile(ServerType type, String configName) {
        return Path.of(getServerPath(type).toString(), configName).toAbsolutePath().toFile();
    }

    public Path getConfigFilePath(ServerType type, String configName) {
        return Path.of(getServerPath(type).toString(), configName);
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

        throw new RuntimeException(
                new FileNotFoundException("Couldn't find any server executable for '" + type + "' server")
        );
    }

    private Path getServerExecutable(ServerType type) {
        return Path.of(getServerPath(type).toString(), Constants.SERVER_EXECUTABLES.get(type));
    }
}
