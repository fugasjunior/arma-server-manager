package cz.forgottenempire.servermanager.journey;

import com.jayway.jsonpath.JsonPath;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import org.springframework.jdbc.core.JdbcTemplate;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ModPresetCrudJourneyTest extends AbstractIntegrationTest {

    @Autowired
    WorkshopModsService workshopModsService;

    @Autowired
    JdbcTemplate jdbcTemplate;

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

    private void markArma3AsInstalled() {
        jdbcTemplate.update("UPDATE server_installation SET installation_status = 'FINISHED' WHERE type = 'ARMA3'");
    }

    @Test
    void whenImportLauncherPresetWithName_thenPresetCreatedWithGivenName() throws Exception {
        markArma3AsInstalled();
        String htmlContent = """
                <html>
                  <head><meta name="arma:PresetName" content="Ignored Name" /></head>
                  <body>
                    <div class="mod-list">
                      <a href="http://steamcommunity.com/sharedfiles/filedetails/?id=%d"></a>
                    </div>
                  </body>
                </html>
                """.formatted(modId);

        MockMultipartFile file = new MockMultipartFile("preset", "preset.html", "text/html", htmlContent.getBytes());

        mockMvc.perform(multipart("/api/mod/launcher_preset")
                        .file(file)
                        .param("name", "My Custom Preset")
                        .header("Authorization", "Bearer " + auth.getToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("My Custom Preset")));
    }

    @Test
    void whenImportLauncherPresetWithDuplicateName_thenBadRequest() throws Exception {
        String presetBody = "{\"name\":\"Existing Preset\",\"mods\":[%d],\"type\":\"ARMA3\"}".formatted(modId);
        api().postJson("/api/mod/preset", presetBody).andExpect(status().isOk());

        String htmlContent = """
                <html>
                  <body>
                    <div class="mod-list">
                      <a href="http://steamcommunity.com/sharedfiles/filedetails/?id=%d"></a>
                    </div>
                  </body>
                </html>
                """.formatted(modId);

        MockMultipartFile file = new MockMultipartFile("preset", "preset.html", "text/html", htmlContent.getBytes());

        mockMvc.perform(multipart("/api/mod/launcher_preset")
                        .file(file)
                        .param("name", "Existing Preset")
                        .header("Authorization", "Bearer " + auth.getToken()))
                .andExpect(status().isConflict());
    }

    @Test
    void whenRenamePreset_thenPresetNameUpdated() throws Exception {
        String presetBody = "{\"name\":\"Original Name\",\"mods\":[%d],\"type\":\"ARMA3\"}".formatted(modId);
        MvcResult createResult = api().postJson("/api/mod/preset", presetBody)
                .andExpect(status().isOk())
                .andReturn();
        int presetId = (int) JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().patch("/api/mod/preset/" + presetId, "{\"name\":\"Renamed Preset\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Renamed Preset")));

        api().get("/api/mod/preset/" + presetId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Renamed Preset")));
    }

    @Test
    void whenRenamePresetToDuplicateName_thenBadRequest() throws Exception {
        String body1 = "{\"name\":\"Preset One\",\"mods\":[%d],\"type\":\"ARMA3\"}".formatted(modId);
        String body2 = "{\"name\":\"Preset Two\",\"mods\":[%d],\"type\":\"ARMA3\"}".formatted(modId);

        api().postJson("/api/mod/preset", body1).andExpect(status().isOk());
        MvcResult createResult = api().postJson("/api/mod/preset", body2).andExpect(status().isOk()).andReturn();
        int preset2Id = (int) JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().patch("/api/mod/preset/" + preset2Id, "{\"name\":\"Preset One\"}")
                .andExpect(status().isConflict());
    }
}
