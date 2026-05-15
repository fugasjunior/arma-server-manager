package cz.forgottenempire.servermanager.journey;

import com.jayway.jsonpath.JsonPath;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.support.dsl.Builders;
import cz.forgottenempire.servermanager.support.fakes.FakeProcess;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServerLifecycleJourneyTest extends AbstractIntegrationTest {

    @Test
    void server_createStartStop_happyPath() throws Exception {
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());

        MvcResult createResult = api().post("/api/server", Builders.arma3Server("IntegrationTestServer", 2302))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", equalTo("IntegrationTestServer")))
                .andExpect(jsonPath("$.type", equalTo("ARMA3")))
                .andReturn();

        int serverId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().get("/api/server/" + serverId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(serverId)));

        api().post("/api/server/" + serverId + "/start")
                .andExpect(status().isOk());

        api().post("/api/server/" + serverId + "/stop")
                .andExpect(status().isOk());

        api().delete("/api/server/" + serverId)
                .andExpect(status().isNoContent());

        api().get("/api/server/" + serverId)
                .andExpect(status().isNotFound());
    }

    @Test
    void server_listAll_returnsCreatedServers() throws Exception {
        api().post("/api/server", Builders.arma3Server("ListTestServer", 2402))
                .andExpect(status().isCreated());

        api().get("/api/server")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.servers[*].name", hasItem("ListTestServer")));
    }
}
