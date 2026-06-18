package cz.forgottenempire.servermanager.journey;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.steamauth.SteamAuthService;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.workshop.WorkshopModsService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkshopModInstallJourneyTest extends AbstractIntegrationTest {

    private static final long MOD_ID = 450814997L;

    @Autowired
    WorkshopModsService workshopModsService;

    @Autowired
    SteamAuthService steamAuthService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update(
                "UPDATE server_installation SET installation_status = 'FINISHED' WHERE type = 'ARMA3'"
        );
        steamAuthService.saveCredentials("testuser", "testpass");
    }

    @AfterEach
    void cleanUp() {
        workshopModsService.getMod(MOD_ID).ifPresent(workshopModsService::deleteMod);
        jdbcTemplate.update(
                "UPDATE server_installation SET installation_status = NULL WHERE type = 'ARMA3'"
        );
        steamAuthService.clearAuthAccount();
    }

    @Test
    void workshopMod_installAndList_happyPath() throws Exception {
        fakeProcesses.scriptSteamCmdWithFixture("workshop-download-success.txt");

        api().postWithQueryParam("/api/mod", "modIds", String.valueOf(MOD_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workshopMods", hasSize(1)))
                .andExpect(jsonPath("$.workshopMods[0].id", equalTo((int) MOD_ID)));

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    var installStatus = workshopModsService.getMod(MOD_ID)
                            .map(m -> m.getInstallationStatus())
                            .orElse(null);
                    assertThat(installStatus, not(equalTo(InstallationStatus.INSTALLATION_IN_PROGRESS)));
                });

        api().get("/api/mod")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workshopMods[*].id", hasItem((int) MOD_ID)));

        api().get("/api/mod/" + MOD_ID)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo((int) MOD_ID)));

        api().delete("/api/mod/" + MOD_ID)
                .andExpect(status().isNoContent());
    }
}
