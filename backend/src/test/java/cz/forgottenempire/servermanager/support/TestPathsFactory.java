package cz.forgottenempire.servermanager.support;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
@Primary
public class TestPathsFactory extends PathsFactory {

    private File fakeSteamCmdExecutable;
    private File fakeServerExecutable;

    public TestPathsFactory(
            @Value("${directory.servers}") String serversPathString,
            @Value("${directory.mods}") String modsPathString,
            @Value("${directory.logs}") String logsPathString,
            @Value("${steamcmd.path}") String steamCmdPathString,
            @Value("${steamcmd.cache.path}") String steamCmdCacheFilePath
    ) {
        super(serversPathString, modsPathString, logsPathString, steamCmdPathString, steamCmdCacheFilePath);
    }

    @PostConstruct
    void createTestDirectoriesAndFiles() throws IOException {
        Path base = Path.of(System.getProperty("java.io.tmpdir"), "arma-server-manager-test");

        Files.createDirectories(base.resolve("servers"));
        Files.createDirectories(base.resolve("mods"));
        Files.createDirectories(base.resolve("logs").resolve("steamcmd"));
        Files.createDirectories(base.resolve("servers").resolve("ARMA3").resolve("mpmissions"));
        Files.createDirectories(base.resolve("servers").resolve("ARMA3").resolve("keys"));
        Files.createDirectories(base.resolve("servers").resolve("DAYZ"));
        Files.createDirectories(base.resolve("servers").resolve("REFORGER"));

        fakeSteamCmdExecutable = base.resolve("steamcmd").toFile();
        if (!fakeSteamCmdExecutable.exists()) {
            fakeSteamCmdExecutable.createNewFile();
            fakeSteamCmdExecutable.setExecutable(true);
        }

        fakeServerExecutable = base.resolve("servers").resolve("ARMA3").resolve("arma3server_x64").toFile();
        if (!fakeServerExecutable.exists()) {
            fakeServerExecutable.createNewFile();
            fakeServerExecutable.setExecutable(true);
        }
    }

    @Override
    public File getSteamCmdExecutable() {
        return fakeSteamCmdExecutable;
    }

    @Override
    public File getServerExecutableWithFallback(ServerType type) {
        return fakeServerExecutable;
    }
}
