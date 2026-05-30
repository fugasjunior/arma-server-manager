package cz.forgottenempire.servermanager.journey;

import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SteamAuthJourneyTest extends AbstractIntegrationTest {

    private static final String CREDENTIALS =
            "{\"username\":\"steamuser\",\"password\":\"steampass\",\"steamGuardToken\":null}";

    @Test
    void steamAuth_verifyAndPersistCredentials_happyPath() throws Exception {
        fakeProcesses.scriptSteamCmdWithFixture("login-success.txt");

        api().postJson("/api/config/auth/verify", CREDENTIALS)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo("SUCCESS")));

        api().postJson("/api/config/auth", CREDENTIALS)
                .andExpect(status().isOk());

        api().get("/api/config/auth")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo("steamuser")));

        api().get("/api/config/auth/status")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isConfigured", is(true)));

        api().delete("/api/config/auth")
                .andExpect(status().isNoContent());

        api().get("/api/config/auth/status")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isConfigured", is(false)));
    }
}
