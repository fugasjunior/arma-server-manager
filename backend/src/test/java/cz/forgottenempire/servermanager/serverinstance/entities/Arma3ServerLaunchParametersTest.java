package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.localmod.LocalMod;
import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class Arma3ServerLaunchParametersTest {

    private Arma3ServerActiveMod workshopEntry(long id, String name, boolean serverOnly, int order) {
        WorkshopMod mod = new WorkshopMod(id);
        mod.setName(name);
        mod.setServerOnly(serverOnly);
        Arma3ServerActiveMod entry = new Arma3ServerActiveMod();
        entry.setMod(mod);
        entry.setPosition(order);
        return entry;
    }

    private Arma3ServerActiveMod workshopEntry(long id, String name, boolean serverOnly, boolean loadOnHc, int order) {
        Arma3ServerActiveMod entry = workshopEntry(id, name, serverOnly, order);
        entry.getMod().setLoadOnHeadlessClient(loadOnHc);
        return entry;
    }

    private Arma3ServerActiveLocalMod localEntry(long id, String name, boolean serverOnly, int order) {
        LocalMod mod = new LocalMod();
        mod.setId(id);
        mod.setName(name);
        mod.setServerOnly(serverOnly);
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
                workshopEntry(3L, "ModC", false, 0),
                workshopEntry(1L, "ModA", false, 1),
                workshopEntry(2L, "ModB", false, 2)
        ));

        List<String> params = server.getModsAsParameters(null);

        List<String> clientParams = params.stream().filter(p -> p.startsWith("-mod=")).toList();
        assertThat(clientParams).containsExactly("-mod=@ModC", "-mod=@ModA", "-mod=@ModB");
    }

    @Test
    void getModsAsParameters_serverModsPreserveOrder() {
        Arma3Server server = serverWithWorkshopMods(List.of(
                workshopEntry(30L, "ServerModZ", true, 0),
                workshopEntry(10L, "ServerModA", true, 1)
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
                workshopEntry(3L, "ClientC", false, 0),
                workshopEntry(30L, "ServerZ", true, 1),
                workshopEntry(1L, "ClientA", false, 2),
                workshopEntry(10L, "ServerA", true, 3)
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
                workshopEntry(1L, "AlwaysLoad", false, true, 0),
                workshopEntry(2L, "SkipOnHc",  false, false, 1)
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
                workshopEntry(1L, "ServerMod", true, true, 0)   // serverOnly=true, loadOnHc=true
        ));

        List<String> hcParams = server.getHeadlessClientModsAsParameters().toList();

        // serverOnly is irrelevant to HC; loadOnHeadlessClient=true means load on HC
        assertThat(hcParams).containsExactly("-mod=@ServerMod");
    }

    @Test
    void getModsAsParameters_interleavedWorkshopAndLocalMods_respectsUnifiedOrder() {
        Arma3Server server = new Arma3Server();
        server.setActiveDLCs(List.of());
        // Intended order: workshopA(0), localB(1), workshopC(2), localD(3)
        server.setActiveMods(List.of(
                workshopEntry(1L, "WorkshopA", false, 0),
                workshopEntry(3L, "WorkshopC", false, 2)
        ));
        server.setActiveLocalMods(List.of(
                localEntry(2L, "LocalB", false, 1),
                localEntry(4L, "LocalD", false, 3)
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
}
