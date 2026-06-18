package cz.forgottenempire.servermanager.journey;

import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SteamAuthJourneyTest extends AbstractIntegrationTest {

    private static final String LOGIN_REQUEST =
            "{\"username\":\"steamuser\",\"password\":\"steampass\"}";

    @Test
    void steamAuth_loginAndPersistCredentials_happyPath() throws Exception {
        fakeProcesses.scriptSteamCmdWithFixture("login-success.txt");

        api().postJson("/api/config/auth/login", LOGIN_REQUEST)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", equalTo("SUCCESS")));

        api().get("/api/config/auth")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo("steamuser")));

        api().get("/api/config/auth/status")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isConfigured", is(true)))
                .andExpect(jsonPath("$.sessionStatus", equalTo("ACTIVE")));

        api().delete("/api/config/auth")
                .andExpect(status().isNoContent());

        api().get("/api/config/auth/status")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isConfigured", is(false)))
                .andExpect(jsonPath("$.sessionStatus", equalTo("NOT_CONFIGURED")));
    }
}
