package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Arma3ServerLaunchParametersTest {

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

    private Arma3Server serverWithMods(List<WorkshopMod> mods) {
        Arma3Server server = new Arma3Server();
        server.setActiveMods(mods);
        server.setActiveDLCs(List.of());
        return server;
    }

    @Test
    void getModsAsParameters_preservesActiveModsOrder() {
        WorkshopMod modC = clientMod(3L, "ModC");
        WorkshopMod modA = clientMod(1L, "ModA");
        WorkshopMod modB = clientMod(2L, "ModB");
        Arma3Server server = serverWithMods(List.of(modC, modA, modB));

        List<String> params = server.getModsAsParameters(null);

        List<String> clientParams = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(clientParams).containsExactly("-mod=@ModC", "-mod=@ModA", "-mod=@ModB");
    }

    @Test
    void getModsAsParameters_serverModsPreserveOrder() {
        WorkshopMod serverModZ = serverMod(30L, "ServerModZ");
        WorkshopMod serverModA = serverMod(10L, "ServerModA");
        Arma3Server server = serverWithMods(List.of(serverModZ, serverModA));

        List<String> params = server.getModsAsParameters(null);

        List<String> serverParams = params.stream().filter(p -> p.startsWith("-serverMod=")).toList();
        assertThat(serverParams).containsExactly("-serverMod=@ServerModZ", "-serverMod=@ServerModA");
    }

    @Test
    void getCreatorDlcsAsParameters_preservesOrder() {
        Arma3Server server = new Arma3Server();
        server.setActiveMods(List.of());
        // SPEARHEAD_1944 (spe), CSLA_IRON_CURTAIN (csla), REACTION_FORCES (rf) — deliberate non-alphabetical order
        server.setActiveDLCs(List.of(Arma3CDLC.SPEARHEAD_1944, Arma3CDLC.CSLA_IRON_CURTAIN, Arma3CDLC.REACTION_FORCES));

        List<String> params = server.getModsAsParameters(null);

        List<String> dlcParams = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(dlcParams).containsExactly("-mod=spe", "-mod=csla", "-mod=rf");
    }

    @Test
    void getModsAsParameters_mixedMods_eachGroupPreservesOrder() {
        WorkshopMod clientC = clientMod(3L, "ClientC");
        WorkshopMod serverZ = serverMod(30L, "ServerZ");
        WorkshopMod clientA = clientMod(1L, "ClientA");
        WorkshopMod serverA = serverMod(10L, "ServerA");
        Arma3Server server = serverWithMods(List.of(clientC, serverZ, clientA, serverA));

        List<String> params = server.getModsAsParameters(null);

        List<String> serverMods = params.stream().filter(p -> p.startsWith("-serverMod=")).toList();
        List<String> clientMods = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(serverMods).containsExactly("-serverMod=@ServerZ", "-serverMod=@ServerA");
        assertThat(clientMods).containsExactly("-mod=@ClientC", "-mod=@ClientA");
    }
}
