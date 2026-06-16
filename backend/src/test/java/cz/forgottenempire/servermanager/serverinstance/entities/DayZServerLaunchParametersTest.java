package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.Arma3KeyService;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.localmod.LocalMod;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

class DayZServerLaunchParametersTest {

    private DayZServerActiveMod workshopEntry(long id, String name, boolean loadOnClient, boolean loadOnServer, int order) {
        WorkshopMod mod = new WorkshopMod(id);
        mod.setName(name);
        mod.setLoadOnClient(loadOnClient);
        mod.setLoadOnServer(loadOnServer);
        DayZServerActiveMod entry = new DayZServerActiveMod();
        entry.setMod(mod);
        entry.setPosition(order);
        return entry;
    }

    private DayZServerActiveLocalMod localEntry(long id, String name, boolean loadOnClient, boolean loadOnServer, int order) {
        LocalMod mod = new LocalMod();
        mod.setId(id);
        mod.setName(name);
        mod.setLoadOnClient(loadOnClient);
        mod.setLoadOnServer(loadOnServer);
        DayZServerActiveLocalMod entry = new DayZServerActiveLocalMod();
        entry.setMod(mod);
        entry.setPosition(order);
        return entry;
    }

    private ServerLaunchContext mockLaunchContext() {
        PathsFactory pathsFactory = mock(PathsFactory.class, withSettings().stubOnly());
        when(pathsFactory.getConfigFilePath(any(), any())).thenReturn(Path.of("/tmp/dayz.cfg"));
        return new ServerLaunchContext(
                pathsFactory,
                mock(Arma3InstancePaths.class, withSettings().stubOnly()),
                mock(Arma3KeyService.class, withSettings().stubOnly()),
                mock(FreeMarkerConfigurer.class, withSettings().stubOnly()));
    }

    @Test
    void getLaunchParameters_preservesClientModsOrder() {
        DayZServer server = new DayZServer();
        server.setId(1L);
        server.setActiveMods(List.of(
                workshopEntry(3L, "ModC", true, true, 0),
                workshopEntry(1L, "ModA", true, true, 1),
                workshopEntry(2L, "ModB", true, true, 2)
        ));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        String modParam = params.stream().filter(p -> p.startsWith("-mod=")).findFirst().orElse("");
        assertThat(modParam).isEqualTo("-mod=@ModC;@ModA;@ModB;");
    }

    @Test
    void getLaunchParameters_preservesServerModsOrder() {
        DayZServer server = new DayZServer();
        server.setId(1L);
        server.setActiveMods(List.of(
                workshopEntry(30L, "SrvZ", false, true, 0),
                workshopEntry(10L, "SrvA", false, true, 1)
        ));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        String modParam = params.stream().filter(p -> p.startsWith("-serverMod=")).findFirst().orElse("");
        assertThat(modParam).isEqualTo("-serverMod=@SrvZ;@SrvA;");
    }

    @Test
    void getLaunchParameters_interleavedWorkshopAndLocalMods_respectsUnifiedOrder() {
        DayZServer server = new DayZServer();
        server.setId(1L);
        // Intended order: workshopA(0), localB(1), workshopC(2)
        server.setActiveMods(List.of(
                workshopEntry(1L, "WorkshopA", true, true, 0),
                workshopEntry(3L, "WorkshopC", true, true, 2)
        ));
        server.setActiveLocalMods(List.of(
                localEntry(2L, "LocalB", true, true, 1)
        ));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        String modParam = params.stream().filter(p -> p.startsWith("-mod=")).findFirst().orElse("");
        assertThat(modParam).isEqualTo("-mod=@WorkshopA;LocalB;@WorkshopC;");
    }

    @Test
    void getLaunchParameters_clientAndServerLoad() {
        DayZServer server = new DayZServer();
        server.setId(1L);
        server.setActiveMods(List.of(
                workshopEntry(1L, "BothLoad", true, true, 0)
        ));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        String modParam = params.stream().filter(p -> p.startsWith("-mod=")).findFirst().orElse("");
        assertThat(modParam).isEqualTo("-mod=@BothLoad;");
    }

    @Test
    void getLaunchParameters_serverLoadOnly() {
        DayZServer server = new DayZServer();
        server.setId(1L);
        server.setActiveMods(List.of(
                workshopEntry(1L, "ServerOnly", false, true, 0)
        ));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        String serverModParam = params.stream().filter(p -> p.startsWith("-serverMod=")).findFirst().orElse("");
        assertThat(serverModParam).isEqualTo("-serverMod=@ServerOnly;");
    }

    @Test
    void getLaunchParameters_noFlagsSetNotInAnyParams() {
        DayZServer server = new DayZServer();
        server.setId(1L);
        server.setActiveMods(List.of(
                workshopEntry(1L, "Unused", false, false, 0)
        ));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        boolean hasModParam = params.stream().anyMatch(p -> p.startsWith("-mod="));
        boolean hasServerModParam = params.stream().anyMatch(p -> p.startsWith("-serverMod="));
        assertThat(hasModParam).isFalse();
        assertThat(hasServerModParam).isFalse();
    }

    @Test
    void getLaunchParameters_clientLoadOnly() {
        DayZServer server = new DayZServer();
        server.setId(1L);
        server.setActiveMods(List.of(
                workshopEntry(1L, "ClientOnly", true, false, 0)
        ));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        boolean hasModParam = params.stream().anyMatch(p -> p.startsWith("-mod="));
        assertThat(hasModParam).isFalse();
    }
}
