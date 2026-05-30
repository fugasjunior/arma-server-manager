package cz.forgottenempire.servermanager.serverinstance.entities;

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

    private DayZServerActiveMod workshopEntry(long id, String name, boolean serverOnly, int order) {
        WorkshopMod mod = new WorkshopMod(id);
        mod.setName(name);
        mod.setServerOnly(serverOnly);
        DayZServerActiveMod entry = new DayZServerActiveMod();
        entry.setMod(mod);
        entry.setPosition(order);
        return entry;
    }

    private DayZServerActiveLocalMod localEntry(long id, String name, boolean serverOnly, int order) {
        LocalMod mod = new LocalMod();
        mod.setId(id);
        mod.setName(name);
        mod.setServerOnly(serverOnly);
        DayZServerActiveLocalMod entry = new DayZServerActiveLocalMod();
        entry.setMod(mod);
        entry.setPosition(order);
        return entry;
    }

    private ServerLaunchContext mockLaunchContext() {
        PathsFactory pathsFactory = mock(PathsFactory.class, withSettings().stubOnly());
        when(pathsFactory.getConfigFilePath(any(), any())).thenReturn(Path.of("/tmp/dayz.cfg"));
        return new ServerLaunchContext(pathsFactory, mock(FreeMarkerConfigurer.class, withSettings().stubOnly()));
    }

    @Test
    void getLaunchParameters_preservesClientModsOrder() {
        DayZServer server = new DayZServer();
        server.setId(1L);
        server.setActiveMods(List.of(
                workshopEntry(3L, "ModC", false, 0),
                workshopEntry(1L, "ModA", false, 1),
                workshopEntry(2L, "ModB", false, 2)
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
                workshopEntry(30L, "SrvZ", true, 0),
                workshopEntry(10L, "SrvA", true, 1)
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
                workshopEntry(1L, "WorkshopA", false, 0),
                workshopEntry(3L, "WorkshopC", false, 2)
        ));
        server.setActiveLocalMods(List.of(
                localEntry(2L, "LocalB", false, 1)
        ));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        String modParam = params.stream().filter(p -> p.startsWith("-mod=")).findFirst().orElse("");
        assertThat(modParam).isEqualTo("-mod=@WorkshopA;LocalB;@WorkshopC;");
    }
}
