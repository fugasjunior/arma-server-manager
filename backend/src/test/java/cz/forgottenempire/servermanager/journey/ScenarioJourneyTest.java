package cz.forgottenempire.servermanager.journey;

import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ScenarioJourneyTest extends AbstractIntegrationTest {

    @Test
    void scenario_uploadListDelete_happyPath() throws Exception {
        File pbo = Files.createTempFile("test-mission", ".pbo").toFile();
        pbo.deleteOnExit();
        try (FileWriter w = new FileWriter(pbo)) {
            w.write("FAKEPBODATA");
        }

        api().multipartPost("/api/scenarios", "file", pbo, "application/octet-stream")
                .andExpect(status().isOk());

        api().get("/api/scenarios")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenarios[*].name", hasItem(pbo.getName())));

        api().delete("/api/scenarios/" + pbo.getName())
                .andExpect(status().isNoContent());

        api().get("/api/scenarios")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenarios[*].name", not(hasItem(pbo.getName()))));
    }
}
