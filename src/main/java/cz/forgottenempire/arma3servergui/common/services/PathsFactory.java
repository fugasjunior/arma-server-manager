package cz.forgottenempire.arma3servergui.common.services;

import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.server.ServerType;
import java.io.File;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PathsFactory {

    private final Path modsBasePath;
    private final Path serversBasePath;
    private final boolean use64bitExec;

    @Autowired
    public PathsFactory(
            @Value("${directory.servers}") String serversPathString,
            @Value("${directory.mods}") String modsPathString,
            @Value("${arma3server.x64}") boolean use64bitExec
    ) {
        serversBasePath = Path.of(serversPathString);
        modsBasePath = Path.of(modsPathString);
        this.use64bitExec = use64bitExec;
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

    public Path getArma3ServerExecutable() {
        String executable = use64bitExec ? "arma3server_x64" :  "arma3server";
        return Path.of(getServerPath(ServerType.ARMA3).toString(), executable);
    }

    public Path getArma3ServerConfigFile(String configName) {
        return Path.of(getServerPath(ServerType.ARMA3).toString(), configName);
    }
}
