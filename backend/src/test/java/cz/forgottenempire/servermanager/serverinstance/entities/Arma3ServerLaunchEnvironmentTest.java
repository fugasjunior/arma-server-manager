package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.Arma3KeyService;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class Arma3ServerLaunchEnvironmentTest {

    private static final long SERVER_ID = 42L;
    private static final int SERVER_PORT = 2302;

    private Arma3Server server;
    private Arma3InstancePaths arma3InstancePaths;
    private ServerLaunchContext ctx;

    @BeforeEach
    void setUp() {
        server = new Arma3Server();
        server.setId(SERVER_ID);
        server.setPort(SERVER_PORT);
        server.setActiveMods(List.of());
        server.setActiveLocalMods(List.of());
        server.setActiveDLCs(List.of());

        arma3InstancePaths = mock(Arma3InstancePaths.class, withSettings().stubOnly());
        when(arma3InstancePaths.getInstanceProfilesPath(SERVER_ID))
                .thenReturn(Path.of("/servers/ARMA3/profiles/ARMA3_42"));
        when(arma3InstancePaths.getInstanceConfigsPath(SERVER_ID))
                .thenReturn(Path.of("/servers/ARMA3/profiles/ARMA3_42/configs"));
        when(arma3InstancePaths.getInstanceKeysPath(SERVER_ID))
                .thenReturn(Path.of("/servers/ARMA3/profiles/ARMA3_42/keys"));
        when(arma3InstancePaths.getInstanceMpmissionsPath(SERVER_ID))
                .thenReturn(Path.of("/servers/ARMA3/profiles/ARMA3_42/mpmissions"));
        when(arma3InstancePaths.getInstanceMpmissionsRelativePath(SERVER_ID))
                .thenReturn(Path.of("profiles/ARMA3_42/mpmissions"));

        ctx = new ServerLaunchContext(
                mock(PathsFactory.class, withSettings().stubOnly()),
                arma3InstancePaths,
                mock(Arma3KeyService.class, withSettings().stubOnly()),
                mock(FreeMarkerConfigurer.class, withSettings().stubOnly()));
    }

    @Test
    void getLaunchParameters_containsMpmissionsFlag() {
        List<String> params = server.getLaunchParameters(ctx);

        assertThat(params).anyMatch(p -> p.equals("-mpmissions=\"profiles/ARMA3_42/mpmissions\""));
        assertThat(params).noneMatch(p -> p.startsWith("-mpmissions=\"/"));
    }

    @Test
    void getLaunchParameters_containsKeysFolderFlag() {
        List<String> params = server.getLaunchParameters(ctx);

        assertThat(params).anyMatch(p -> p.startsWith("-keysFolder="));
        assertThat(params).anyMatch(p -> p.contains("profiles/ARMA3_42/keys"));
    }

    @Test
    void getLaunchParameters_profilesPointsToInstanceBase() {
        List<String> params = server.getLaunchParameters(ctx);

        assertThat(params).anyMatch(p -> p.startsWith("-profiles=") && p.contains("profiles/ARMA3_42"));
    }

    @Test
    void getLaunchParameters_configPointsToInstanceConfigsDir() {
        List<String> params = server.getLaunchParameters(ctx);

        assertThat(params).anyMatch(p -> p.startsWith("-config=") && p.contains("ARMA3_42/configs"));
    }

    @Test
    void prepareLaunchEnvironment_createsKeysAndMpmissionsDirectories(@TempDir Path tempDir) throws IOException {
        PathsFactory realPathsFactory = new PathsFactory(
                tempDir.toString(),
                tempDir.resolve("mods").toString(),
                tempDir.resolve("logs").toString(),
                "/steamcmd/steamcmd",
                "/steamcmd/cache.json");
        Arma3InstancePaths realInstancePaths = new Arma3InstancePaths(realPathsFactory);
        Arma3KeyService realKeyService = new Arma3KeyService(realPathsFactory, realInstancePaths);
        ServerLaunchContext realCtx = new ServerLaunchContext(
                mock(PathsFactory.class, withSettings().stubOnly()),
                realInstancePaths,
                realKeyService,
                mock(FreeMarkerConfigurer.class, withSettings().stubOnly()));

        server.prepareLaunchEnvironment(realCtx);

        assertThat(realInstancePaths.getInstanceKeysPath(SERVER_ID)).isDirectory();
        assertThat(realInstancePaths.getInstanceMpmissionsPath(SERVER_ID)).isDirectory();
    }
}
