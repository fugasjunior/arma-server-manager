package cz.forgottenempire.servermanager.journey;

import com.jayway.jsonpath.JsonPath;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.support.dsl.Builders;
import cz.forgottenempire.servermanager.support.fakes.FakeProcess;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HeadlessClientJourneyTest extends AbstractIntegrationTest {

    @Test
    void headlessClient_addAndRemove_happyPath() throws Exception {
        // Server process stays alive for the duration of the test
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());
        // HC is also a server process
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());

        MvcResult createResult = api().post("/api/server", Builders.arma3Server("HCTestServer", 2502))
                .andExpect(status().isCreated())
                .andReturn();

        int serverId = (int) JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().post("/api/server/" + serverId + "/start")
                .andExpect(status().isOk());

        api().post("/api/server/" + serverId + "/hc/start")
                .andExpect(status().isOk());

        api().delete("/api/server/" + serverId + "/hc/stop")
                .andExpect(status().isOk());

        api().post("/api/server/" + serverId + "/stop")
                .andExpect(status().isOk());
    }
}
