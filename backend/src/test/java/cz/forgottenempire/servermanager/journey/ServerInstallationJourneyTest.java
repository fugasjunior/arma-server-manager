package cz.forgottenempire.servermanager.journey;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.steamauth.SteamAuthService;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.workshop.SteamAuthDto;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.*;

class ServerInstallationJourneyTest extends AbstractIntegrationTest {

    @Autowired
    SteamAuthService steamAuthService;

    @BeforeEach
    void setUp() {
        steamAuthService.setAuthAccount(new SteamAuthDto("testuser", "testpass", null));
    }

    @AfterEach
    void tearDown() {
        steamAuthService.clearAuthAccount();
    }

    @Test
    void serverInstallation_triggerAndComplete_happyPath() throws Exception {
        fakeProcesses.scriptSteamCmdWithFixture("app-update-success.txt");

        api().get("/api/server/installation/ARMA3")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.installationStatus",
                        not(equalTo(InstallationStatus.INSTALLATION_IN_PROGRESS.name()))));

        api().post("/api/server/installation/ARMA3")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.installationStatus",
                        equalTo(InstallationStatus.INSTALLATION_IN_PROGRESS.name())));

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() ->
                        api().get("/api/server/installation/ARMA3")
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.installationStatus",
                                        equalTo(InstallationStatus.FINISHED.name())))
                );
    }
}
