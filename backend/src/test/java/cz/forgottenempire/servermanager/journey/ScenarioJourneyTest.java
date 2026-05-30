package cz.forgottenempire.servermanager.journey;

import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
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

        api().deleteWithQueryParam("/api/scenarios", "name", pbo.getName())
                .andExpect(status().isNoContent());

        api().get("/api/scenarios")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenarios[*].name", not(hasItem(pbo.getName()))));
    }

    @Test
    void scenario_uploadListDelete_withHashInFilename() throws Exception {
        // Regression test for https://github.com/fugasjunior/arma-server-manager/issues/160
        // Filenames with '#' were truncated at the hash when used as a path parameter,
        // causing a NoSuchFileException. Using a query parameter fixes the encoding issue.
        // Files.createTempFile with prefix "TheBig#Mess" produces a name containing '#'.
        File pbo = Files.createTempFile("TheBig#Mess", ".pbo").toFile();
        pbo.deleteOnExit();
        try (FileWriter w = new FileWriter(pbo)) {
            w.write("FAKEPBODATA");
        }

        api().multipartPost("/api/scenarios", "file", pbo, "application/octet-stream")
                .andExpect(status().isOk());

        api().get("/api/scenarios")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenarios[*].name", hasItem(pbo.getName())));

        api().deleteWithQueryParam("/api/scenarios", "name", pbo.getName())
                .andExpect(status().isNoContent());

        api().get("/api/scenarios")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scenarios[*].name", not(hasItem(pbo.getName()))));
    }
}
