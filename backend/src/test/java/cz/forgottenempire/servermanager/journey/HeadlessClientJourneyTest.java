package cz.forgottenempire.servermanager.journey;

import com.jayway.jsonpath.JsonPath;
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest;
import cz.forgottenempire.servermanager.support.dsl.Builders;
import cz.forgottenempire.servermanager.support.fakes.FakeProcess;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HeadlessClientJourneyTest extends AbstractIntegrationTest {

    @Test
    void headlessClient_setTarget_persistsInDatabase() throws Exception {
        MvcResult createResult = api().post("/api/server", Builders.arma3Server("HCPersistTest", 2502))
                .andExpect(status().isCreated())
                .andReturn();
        int serverId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().put("/api/server/" + serverId + "/hc/target", "{\"targetHeadlessClientsCount\":2}")
                .andExpect(status().isOk());

        api().get("/api/server/" + serverId)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.targetHeadlessClientsCount", equalTo(2)));
    }

    @Test
    void headlessClient_setTargetBeforeStart_spawnsHCsOnServerStart() throws Exception {
        // 1 server + 2 HC processes
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());

        MvcResult createResult = api().post("/api/server", Builders.arma3Server("HCSpawnTest", 2504))
                .andExpect(status().isCreated())
                .andReturn();
        int serverId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().put("/api/server/" + serverId + "/hc/target", "{\"targetHeadlessClientsCount\":2}")
                .andExpect(status().isOk());

        api().post("/api/server/" + serverId + "/start")
                .andExpect(status().isOk());

        api().get("/api/server/" + serverId + "/status")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headlessClientsCount", equalTo(2)));

        api().post("/api/server/" + serverId + "/stop").andExpect(status().isOk());
    }

    @Test
    void headlessClient_setTargetWhileRunning_spawnsAdditionalHC() throws Exception {
        // 1 server + 1 initial HC + 1 extra HC when target increases
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());

        MvcResult createResult = api().post("/api/server", Builders.arma3Server("HCGrowTest", 2506))
                .andExpect(status().isCreated())
                .andReturn();
        int serverId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().put("/api/server/" + serverId + "/hc/target", "{\"targetHeadlessClientsCount\":1}")
                .andExpect(status().isOk());

        api().post("/api/server/" + serverId + "/start")
                .andExpect(status().isOk());

        api().get("/api/server/" + serverId + "/status")
                .andExpect(jsonPath("$.headlessClientsCount", equalTo(1)));

        api().put("/api/server/" + serverId + "/hc/target", "{\"targetHeadlessClientsCount\":2}")
                .andExpect(status().isOk());

        api().get("/api/server/" + serverId + "/status")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headlessClientsCount", equalTo(2)));

        api().post("/api/server/" + serverId + "/stop").andExpect(status().isOk());
    }

    @Test
    void headlessClient_decreaseTarget_stopsExtraHCs() throws Exception {
        // 1 server + 2 HCs initially
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());

        MvcResult createResult = api().post("/api/server", Builders.arma3Server("HCShrinkTest", 2508))
                .andExpect(status().isCreated())
                .andReturn();
        int serverId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().put("/api/server/" + serverId + "/hc/target", "{\"targetHeadlessClientsCount\":2}")
                .andExpect(status().isOk());

        api().post("/api/server/" + serverId + "/start")
                .andExpect(status().isOk());

        api().get("/api/server/" + serverId + "/status")
                .andExpect(jsonPath("$.headlessClientsCount", equalTo(2)));

        api().put("/api/server/" + serverId + "/hc/target", "{\"targetHeadlessClientsCount\":1}")
                .andExpect(status().isOk());

        api().get("/api/server/" + serverId + "/status")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headlessClientsCount", equalTo(1)));

        api().post("/api/server/" + serverId + "/stop").andExpect(status().isOk());
    }

    @Test
    void headlessClient_crashedHC_autoRestartedOnReconcile() throws Exception {
        // server process stays alive; HC crashes immediately; replacement HC stays alive
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());
        fakeProcesses.scriptServerProcess(FakeProcess.exiting(1));     // HC that crashes
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive()); // replacement HC

        MvcResult createResult = api().post("/api/server", Builders.arma3Server("HCRestartTest", 2510))
                .andExpect(status().isCreated())
                .andReturn();
        int serverId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().put("/api/server/" + serverId + "/hc/target", "{\"targetHeadlessClientsCount\":1}")
                .andExpect(status().isOk());

        api().post("/api/server/" + serverId + "/start")
                .andExpect(status().isOk());

        // Re-applying the same target triggers reconcile: prunes the dead HC and spawns the replacement
        api().put("/api/server/" + serverId + "/hc/target", "{\"targetHeadlessClientsCount\":1}")
                .andExpect(status().isOk());

        api().get("/api/server/" + serverId + "/status")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headlessClientsCount", equalTo(1)));

        api().post("/api/server/" + serverId + "/stop").andExpect(status().isOk());
    }

    @Test
    void headlessClient_targetZero_serverStartsWithoutHCs() throws Exception {
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive());

        MvcResult createResult = api().post("/api/server", Builders.arma3Server("HCZeroTest", 2512))
                .andExpect(status().isCreated())
                .andReturn();
        int serverId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.id");

        api().post("/api/server/" + serverId + "/start")
                .andExpect(status().isOk());

        api().get("/api/server/" + serverId + "/status")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.headlessClientsCount", equalTo(0)));

        api().post("/api/server/" + serverId + "/stop").andExpect(status().isOk());
    }
}
