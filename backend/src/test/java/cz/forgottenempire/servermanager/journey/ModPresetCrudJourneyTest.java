package cz.forgottenempire.servermanager.journey;

import com.jayway.jsonpath.JsonPath;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ModPresetCrudJourneyTest extends AbstractIntegrationTest {

    @Autowired
    WorkshopModsService workshopModsService;

    private Long modId;

    @BeforeEach
    void seedMod() {
        WorkshopMod mod = new WorkshopMod();
        mod.setId(450814997L);
        mod.setName("CBA_A3");
        mod.setServerType(ServerType.ARMA3);
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        workshopModsService.saveMod(mod);
        modId = mod.getId();
    }

    @Test
    void modPreset_createReadUpdateDelete_happyPath() throws Exception {
        String presetBody = "{\"name\":\"TestPreset\",\"mods\":[%d],\"type\":\"ARMA3\"}".formatted(modId);

        MvcResult createResult = api().postJson("/api/mod/preset", presetBody)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("TestPreset")))
                .andExpect(jsonPath("$.mods", hasSize(1)))
                .andReturn();

        int presetId = (int) JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().get("/api/mod/preset/" + presetId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("TestPreset")));

        api().get("/api/mod/preset")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.presets[*].name", hasItem("TestPreset")));

        String updateBody = "{\"name\":\"UpdatedPreset\",\"mods\":[%d]}".formatted(modId);
        api().put("/api/mod/preset/" + presetId, updateBody)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("UpdatedPreset")));

        api().delete("/api/mod/preset/" + presetId)
                .andExpect(status().isNoContent());

        api().get("/api/mod/preset/" + presetId)
                .andExpect(status().isNotFound());
    }
}
