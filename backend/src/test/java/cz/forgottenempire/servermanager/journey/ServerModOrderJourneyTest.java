package cz.forgottenempire.servermanager.journey;

import com.jayway.jsonpath.JsonPath;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.support.dsl.Builders;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServerModOrderJourneyTest extends AbstractIntegrationTest {

    @Autowired
    WorkshopModsService workshopModsService;

    private long modIdA;
    private long modIdB;
    private long modIdC;

    @BeforeEach
    void seedMods() {
        WorkshopMod modA = new WorkshopMod();
        modA.setId(700000001L);
        modA.setName("ModA");
        modA.setServerType(ServerType.ARMA3);
        modA.setInstallationStatus(InstallationStatus.FINISHED);
        workshopModsService.saveMod(modA);
        modIdA = modA.getId();

        WorkshopMod modB = new WorkshopMod();
        modB.setId(700000002L);
        modB.setName("ModB");
        modB.setServerType(ServerType.ARMA3);
        modB.setInstallationStatus(InstallationStatus.FINISHED);
        workshopModsService.saveMod(modB);
        modIdB = modB.getId();

        WorkshopMod modC = new WorkshopMod();
        modC.setId(700000003L);
        modC.setName("ModC");
        modC.setServerType(ServerType.ARMA3);
        modC.setInstallationStatus(InstallationStatus.FINISHED);
        workshopModsService.saveMod(modC);
        modIdC = modC.getId();
    }

    @Test
    void whenUpdateServerWithSpecificModOrder_thenModOrderIsPreserved() throws Exception {
        MvcResult createResult = api().post("/api/server", Builders.arma3Server("ModOrderTestServer", 2502))
                .andExpect(status().isCreated())
                .andReturn();
        int serverId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        // Set mods in non-sorted order: C, A, B
        String updateBody = """
                {
                  "id": %d,
                  "type": "ARMA3",
                  "name": "ModOrderTestServer",
                  "port": 2502,
                  "queryPort": 2503,
                  "maxPlayers": 20,
                  "activeMods": [{"id": %d, "name": "ModC", "position": 0}, {"id": %d, "name": "ModA", "position": 1}, {"id": %d, "name": "ModB", "position": 2}],
                  "activeDLCs": [],
                  "customLaunchParameters": [],
                  "difficultySettings": {}
                }
                """.formatted(serverId, modIdC, modIdA, modIdB);

        api().put("/api/server/" + serverId, updateBody)
                .andExpect(status().isOk());

        api().get("/api/server/" + serverId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeMods[0].id", equalTo((int) modIdC)))
                .andExpect(jsonPath("$.activeMods[1].id", equalTo((int) modIdA)))
                .andExpect(jsonPath("$.activeMods[2].id", equalTo((int) modIdB)));
    }
}
