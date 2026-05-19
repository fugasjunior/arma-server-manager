package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.PathsFactory;
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

    private WorkshopMod clientMod(long id, String name) {
        WorkshopMod mod = new WorkshopMod(id);
        mod.setName(name);
        mod.setServerOnly(false);
        return mod;
    }

    private WorkshopMod serverMod(long id, String name) {
        WorkshopMod mod = new WorkshopMod(id);
        mod.setName(name);
        mod.setServerOnly(true);
        return mod;
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
        server.setActiveMods(List.of(clientMod(3L, "ModC"), clientMod(1L, "ModA"), clientMod(2L, "ModB")));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        String modParam = params.stream().filter(p -> p.startsWith("-mod=")).findFirst().orElse("");
        assertThat(modParam).isEqualTo("-mod=@ModC;@ModA;@ModB;");
    }

    @Test
    void getLaunchParameters_preservesServerModsOrder() {
        DayZServer server = new DayZServer();
        server.setId(1L);
        server.setActiveMods(List.of(serverMod(30L, "SrvZ"), serverMod(10L, "SrvA")));

        List<String> params = server.getLaunchParameters(mockLaunchContext());

        String modParam = params.stream().filter(p -> p.startsWith("-serverMod=")).findFirst().orElse("");
        assertThat(modParam).isEqualTo("-serverMod=@SrvZ;@SrvA;");
    }
}
