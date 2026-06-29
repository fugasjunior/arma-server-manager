package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.localmod.LocalMod;
import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Arma3ServerLaunchParametersTest {

    private Arma3ServerActiveMod workshopEntry(long id, String name, boolean loadOnClient, boolean loadOnServer, boolean loadOnHc, int order) {
        WorkshopMod mod = new WorkshopMod(id);
        mod.setName(name);
        mod.setLoadOnClient(loadOnClient);
        mod.setLoadOnServer(loadOnServer);
        mod.setLoadOnHeadlessClient(loadOnHc);
        Arma3ServerActiveMod entry = new Arma3ServerActiveMod();
        entry.setMod(mod);
        entry.setPosition(order);
        return entry;
    }

    private Arma3ServerActiveLocalMod localEntry(long id, String name, boolean loadOnClient, boolean loadOnServer, boolean loadOnHc, int order) {
        LocalMod mod = new LocalMod();
        mod.setId(id);
        mod.setName(name);
        mod.setLoadOnClient(loadOnClient);
        mod.setLoadOnServer(loadOnServer);
        mod.setLoadOnHeadlessClient(loadOnHc);
        Arma3ServerActiveLocalMod entry = new Arma3ServerActiveLocalMod();
        entry.setMod(mod);
        entry.setPosition(order);
        return entry;
    }

    private Arma3Server serverWithWorkshopMods(List<Arma3ServerActiveMod> mods) {
        Arma3Server server = new Arma3Server();
        server.setActiveMods(mods);
        server.setActiveDLCs(List.of());
        return server;
    }

    @Test
    void getModsAsParameters_preservesActiveModsOrder() {
        Arma3Server server = serverWithWorkshopMods(List.of(
                workshopEntry(3L, "ModC", true, true, false, 0),
                workshopEntry(1L, "ModA", true, true, false, 1),
                workshopEntry(2L, "ModB", true, true, false, 2)
        ));

        List<String> params = server.getModsAsParameters(null);

        List<String> clientParams = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(clientParams).containsExactly("-mod=@ModC", "-mod=@ModA", "-mod=@ModB");
    }

    @Test
    void getModsAsParameters_serverModsPreserveOrder() {
        Arma3Server server = serverWithWorkshopMods(List.of(
                workshopEntry(30L, "ServerModZ", false, true, false, 0),
                workshopEntry(10L, "ServerModA", false, true, false, 1)
        ));

        List<String> params = server.getModsAsParameters(null);

        List<String> serverParams = params.stream().filter(p -> p.startsWith("-serverMod=")).toList();
        assertThat(serverParams).containsExactly("-serverMod=@ServerModZ", "-serverMod=@ServerModA");
    }

    @Test
    void getCreatorDlcsAsParameters_preservesOrder() {
        Arma3Server server = new Arma3Server();
        server.setActiveMods(List.of());
        server.setActiveDLCs(List.of(Arma3CDLC.SPEARHEAD_1944, Arma3CDLC.CSLA_IRON_CURTAIN, Arma3CDLC.REACTION_FORCES));

        List<String> params = server.getModsAsParameters(null);

        List<String> dlcParams = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(dlcParams).containsExactly("-mod=spe", "-mod=csla", "-mod=rf");
    }

    @Test
    void getModsAsParameters_mixedMods_eachGroupPreservesOrder() {
        Arma3Server server = serverWithWorkshopMods(List.of(
                workshopEntry(3L, "ClientC", true, true, false, 0),
                workshopEntry(30L, "ServerZ", false, true, false, 1),
                workshopEntry(1L, "ClientA", true, true, false, 2),
                workshopEntry(10L, "ServerA", false, true, false, 3)
        ));

        List<String> params = server.getModsAsParameters(null);

        List<String> serverMods = params.stream().filter(p -> p.startsWith("-serverMod=")).toList();
        List<String> clientMods = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(serverMods).containsExactly("-serverMod=@ServerZ", "-serverMod=@ServerA");
        assertThat(clientMods).containsExactly("-mod=@ClientC", "-mod=@ClientA");
    }

    @Test
    void getHeadlessClientModsAsParameters_excludesModsWithLoadOnHcFalse() {
        Arma3Server server = new Arma3Server();
        server.setActiveDLCs(List.of());
        server.setActiveLocalMods(List.of());
        server.setActiveMods(List.of(
                workshopEntry(1L, "AlwaysLoad", true, true, true, 0),
                workshopEntry(2L, "SkipOnHc",  true, true, false, 1)
        ));

        List<String> hcParams = server.getHeadlessClientModsAsParameters().toList();
        List<String> allClientParams = server.getClientModsAsParameters().toList();

        // HC only sees AlwaysLoad
        assertThat(hcParams).containsExactly("-mod=@AlwaysLoad");
        // Main server still sees both
        assertThat(allClientParams).containsExactly("-mod=@AlwaysLoad", "-mod=@SkipOnHc");
    }

    @Test
    void getHeadlessClientModsAsParameters_includesServerOnlyModWhenLoadOnHcTrue() {
        Arma3Server server = new Arma3Server();
        server.setActiveDLCs(List.of());
        server.setActiveLocalMods(List.of());
        server.setActiveMods(List.of(
                workshopEntry(1L, "ServerMod", false, true, true, 0)   // loadOnServer=true, loadOnHc=true
        ));

        List<String> hcParams = server.getHeadlessClientModsAsParameters().toList();

        // loadOnHeadlessClient=true means load on HC
        assertThat(hcParams).containsExactly("-mod=@ServerMod");
    }

    @Test
    void getModsAsParameters_interleavedWorkshopAndLocalMods_respectsUnifiedOrder() {
        Arma3Server server = new Arma3Server();
        server.setActiveDLCs(List.of());
        // Intended order: workshopA(0), localB(1), workshopC(2), localD(3)
        server.setActiveMods(List.of(
                workshopEntry(1L, "WorkshopA", true, true, false, 0),
                workshopEntry(3L, "WorkshopC", true, true, false, 2)
        ));
        server.setActiveLocalMods(List.of(
                localEntry(2L, "LocalB", true, true, false, 1),
                localEntry(4L, "LocalD", true, true, false, 3)
        ));

        List<String> params = server.getModsAsParameters(null);

        List<String> clientParams = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(clientParams).containsExactly(
                "-mod=@WorkshopA",
                "-mod=LocalB",
                "-mod=@WorkshopC",
                "-mod=LocalD"
        );
    }

    @Test
    void getModsAsParameters_loadOnClientOnlyNotInServerParams() {
        Arma3Server server = serverWithWorkshopMods(List.of(
                workshopEntry(1L, "ClientOnly", true, false, false, 0)
        ));

        List<String> params = server.getModsAsParameters(null);

        List<String> serverMods = params.stream().filter(p -> p.startsWith("-serverMod=")).toList();
        List<String> clientMods = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(serverMods).isEmpty();
        assertThat(clientMods).isEmpty();
    }

    @Test
    void getModsAsParameters_noFlagsSetNotInAnyParams() {
        Arma3Server server = serverWithWorkshopMods(List.of(
                workshopEntry(1L, "Unused", false, false, false, 0)
        ));

        List<String> params = server.getModsAsParameters(null);

        List<String> serverMods = params.stream().filter(p -> p.startsWith("-serverMod=")).toList();
        List<String> clientMods = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(serverMods).isEmpty();
        assertThat(clientMods).isEmpty();
    }

    @Test
    void getModsAsParameters_serverModWithHcLoad_showsInBoth() {
        Arma3Server server = serverWithWorkshopMods(List.of(
                workshopEntry(1L, "ServerWithHc", false, true, true, 0)
        ));

        List<String> params = server.getModsAsParameters(null);
        List<String> hcParams = server.getHeadlessClientModsAsParameters().toList();

        List<String> serverMods = params.stream().filter(p -> p.startsWith("-serverMod=")).toList();
        List<String> hcMods = hcParams.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(serverMods).containsExactly("-serverMod=@ServerWithHc");
        assertThat(hcMods).containsExactly("-mod=@ServerWithHc");
    }

    @Test
    void getModsAsParameters_clientAndServerLoadNotOnHc() {
        Arma3Server server = serverWithWorkshopMods(List.of(
                workshopEntry(1L, "BothNotHc", true, true, false, 0)
        ));

        List<String> params = server.getModsAsParameters(null);
        List<String> hcParams = server.getHeadlessClientModsAsParameters().toList();

        List<String> clientMods = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        List<String> hcMods = hcParams.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(clientMods).containsExactly("-mod=@BothNotHc");
        assertThat(hcMods).isEmpty();
    }
}
