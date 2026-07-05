package cz.forgottenempire.servermanager.journey;

import com.jayway.jsonpath.JsonPath;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.support.dsl.Builders;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkshopModDeleteJourneyTest extends AbstractIntegrationTest {

    private static final long ARMA3_MOD_ID = 800000001L;
    private static final long DAYZ_MOD_ID = 800000002L;

    @Autowired
    WorkshopModsService workshopModsService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    private Long arma3ServerId;
    private Long dayzServerId;

    @AfterEach
    void cleanUp() {
        if (arma3ServerId != null) {
            try { api().delete("/api/server/" + arma3ServerId); } catch (Exception ignored) {}
        }
        if (dayzServerId != null) {
            try { api().delete("/api/server/" + dayzServerId); } catch (Exception ignored) {}
        }
        workshopModsService.getMod(ARMA3_MOD_ID).ifPresent(workshopModsService::deleteMod);
        workshopModsService.getMod(DAYZ_MOD_ID).ifPresent(workshopModsService::deleteMod);
    }

    @Test
    void whenDeleteModActiveOnArma3Server_thenModAndActiveModEntryAreRemoved() throws Exception {
        WorkshopMod mod = savedMod(ARMA3_MOD_ID, ServerType.ARMA3);
        arma3ServerId = createArma3ServerWithActiveMod(mod);

        api().delete("/api/mod/" + ARMA3_MOD_ID)
                .andExpect(status().isNoContent());

        assertThat(workshopModsService.getMod(ARMA3_MOD_ID)).isEmpty();
        int remainingActiveModRows = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM arma3server_active_mods WHERE arma3server_id = ?",
                Integer.class, arma3ServerId);
        assertThat(remainingActiveModRows).isZero();
    }

    @Test
    void whenDeleteModActiveOnDayZServer_thenModAndActiveModEntryAreRemoved() throws Exception {
        WorkshopMod mod = savedMod(DAYZ_MOD_ID, ServerType.DAYZ);
        dayzServerId = createDayZServerWithActiveMod(mod);

        api().delete("/api/mod/" + DAYZ_MOD_ID)
                .andExpect(status().isNoContent());

        assertThat(workshopModsService.getMod(DAYZ_MOD_ID)).isEmpty();
        int remainingActiveModRows = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM dayzserver_active_mods WHERE dayzserver_id = ?",
                Integer.class, dayzServerId);
        assertThat(remainingActiveModRows).isZero();
    }

    private WorkshopMod savedMod(long id, ServerType serverType) {
        WorkshopMod mod = new WorkshopMod();
        mod.setId(id);
        mod.setName("TestMod-" + id);
        mod.setServerType(serverType);
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        return workshopModsService.saveMod(mod);
    }

    private long createArma3ServerWithActiveMod(WorkshopMod mod) throws Exception {
        MvcResult createResult = api().post("/api/server", Builders.arma3Server("DeleteTestArma3Server", 2600))
                .andExpect(status().isCreated())
                .andReturn();
        long serverId = ((Number) JsonPath.read(createResult.getResponse().getContentAsString(), "$.id")).longValue();

        String updateBody = """
                {
                  "id": %d,
                  "type": "ARMA3",
                  "name": "DeleteTestArma3Server",
                  "port": 2600,
                  "queryPort": 2601,
                  "maxPlayers": 20,
                  "activeMods": [{"id": %d, "name": "%s", "position": 0}],
                  "activeDLCs": [],
                  "customLaunchParameters": [],
                  "difficultySettings": {}
                }
                """.formatted(serverId, mod.getId(), mod.getName());
        api().put("/api/server/" + serverId, updateBody).andExpect(status().isOk());
        return serverId;
    }

    private long createDayZServerWithActiveMod(WorkshopMod mod) throws Exception {
        String createBody = """
                {
                  "type": "DAYZ",
                  "name": "DeleteTestDayZServer",
                  "port": 2610,
                  "queryPort": 2611,
                  "maxPlayers": 20,
                  "activeMods": [],
                  "customLaunchParameters": [],
                  "timeAcceleration": 1.0,
                  "nightTimeAcceleration": 1.0
                }
                """;
        MvcResult createResult = api().postJson("/api/server", createBody)
                .andExpect(status().isCreated())
                .andReturn();
        long serverId = ((Number) JsonPath.read(createResult.getResponse().getContentAsString(), "$.id")).longValue();

        String updateBody = """
                {
                  "id": %d,
                  "type": "DAYZ",
                  "name": "DeleteTestDayZServer",
                  "port": 2610,
                  "queryPort": 2611,
                  "maxPlayers": 20,
                  "activeMods": [{"id": %d, "name": "%s", "position": 0}],
                  "customLaunchParameters": [],
                  "timeAcceleration": 1.0,
                  "nightTimeAcceleration": 1.0
                }
                """.formatted(serverId, mod.getId(), mod.getName());
        api().put("/api/server/" + serverId, updateBody).andExpect(status().isOk());
        return serverId;
    }
}
