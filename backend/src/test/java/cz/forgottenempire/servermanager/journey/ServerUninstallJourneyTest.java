package cz.forgottenempire.servermanager.journey;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.steamauth.SteamAuthService;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.api.model.SteamAuthDto;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServerUninstallJourneyTest extends AbstractIntegrationTest {

    @Autowired
    SteamAuthService steamAuthService;

    @Autowired
    PathsFactory pathsFactory;

    @BeforeEach
    void setUp() {
        steamAuthService.setAuthAccount(new SteamAuthDto().username("testuser").password("testpass"));
    }

    @AfterEach
    void tearDown() {
        steamAuthService.clearAuthAccount();
    }

    @Test
    void uninstallServer_afterInstall_resetsStatusAndDeletesDirectory() throws Exception {
        fakeProcesses.scriptSteamCmdWithFixture("app-update-success.txt");

        api().post("/api/server/installation/ARMA3").andExpect(status().isOk());

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() ->
                        api().get("/api/server/installation/ARMA3")
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.installationStatus",
                                        equalTo(InstallationStatus.FINISHED.name())))
                );

        api().delete("/api/server/installation/ARMA3")
                .andExpect(status().isNoContent());

        api().get("/api/server/installation/ARMA3")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.installationStatus", nullValue()))
                .andExpect(jsonPath("$.version", nullValue()))
                .andExpect(jsonPath("$.lastUpdatedAt", nullValue()));

        assertThat(pathsFactory.getServerPath(cz.forgottenempire.servermanager.common.ServerType.ARMA3))
                .doesNotExist();
    }

    @Test
    void uninstallServer_whileInstallInProgress_returns409() throws Exception {
        fakeProcesses.scriptSteamCmdWithFixture("app-update-success.txt");

        api().post("/api/server/installation/ARMA3")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.installationStatus",
                        equalTo(InstallationStatus.INSTALLATION_IN_PROGRESS.name())));

        api().delete("/api/server/installation/ARMA3")
                .andExpect(status().isConflict());

        Awaitility.await()
                .atMost(Duration.ofSeconds(10))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() ->
                        api().get("/api/server/installation/ARMA3")
                                .andExpect(jsonPath("$.installationStatus",
                                        equalTo(InstallationStatus.FINISHED.name())))
                );
    }
}
