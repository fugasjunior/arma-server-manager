package cz.forgottenempire.servermanager.journey

import com.jayway.jsonpath.JsonPath
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest
import cz.forgottenempire.servermanager.support.dsl.Builders
import cz.forgottenempire.servermanager.support.fakes.FakeProcess
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.nio.file.Files

/**
 * Journey tests for per-instance (server-scoped) scenario endpoints.
 * Endpoints: GET/POST /api/server/{id}/scenarios and GET/DELETE /api/server/{id}/scenarios/{name}.
 */
class ServerScenarioJourneyTest : AbstractIntegrationTest() {

    @Test
    fun `scenario uploadListDownloadDelete happyPath`() {
        val serverId = createAndStartServer(2700)
        try {
            val pboName = "test-mission.pbo"

            // Upload → 201 with updated list in body
            uploadPbo(serverId, pboName)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.scenarios[*].name", hasItem(pboName)))

            // List
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/server/{id}/scenarios", serverId)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.scenarios[*].name", hasItem(pboName)))

            // Download (path param)
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/server/{id}/scenarios/{name}", serverId, pboName)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isOk)

            // Delete (path param)
            mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/server/{id}/scenarios/{name}", serverId, pboName)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isNoContent)

            // Confirm gone
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/server/{id}/scenarios", serverId)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.scenarios[*].name", not(hasItem(pboName))))
        } finally {
            stopAndDeleteServer(serverId)
        }
    }

    /**
     * Regression test for issue #160 (filename containing '#').
     * The original bug was the frontend building the path unencoded, so '#' truncated the URL.
     * Using MockMvc's URI-template form ({name}) mirrors the generated Axios client's
     * encodeURIComponent behaviour — '#' is encoded as '%23' in the outgoing request and
     * decoded back to '#' by Spring's PathPattern, so the endpoint must handle it correctly.
     */
    @Test
    fun `scenario uploadListDelete withHashInFilename`() {
        val serverId = createAndStartServer(2710)
        // Produces a name like "TheBig#Mess12345678.pbo" with a real '#' in it
        val tmpFile = Files.createTempFile("TheBig#Mess", ".pbo").toFile().also {
            it.deleteOnExit()
            it.writeText("FAKEPBODATA")
        }
        val pboName = tmpFile.name

        try {
            uploadPbo(serverId, pboName)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.scenarios[*].name", hasItem(pboName)))

            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/server/{id}/scenarios", serverId)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.scenarios[*].name", hasItem(pboName)))

            // URI-template form encodes '#' → '%23', mirroring encodeURIComponent in Axios
            mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/server/{id}/scenarios/{name}", serverId, pboName)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isNoContent)

            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/server/{id}/scenarios", serverId)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.scenarios[*].name", not(hasItem(pboName))))
        } finally {
            stopAndDeleteServer(serverId)
        }
    }

    @Test
    fun `scenario crossInstanceIsolation`() {
        val serverA = createAndStartServer(2720)
        val serverB = createAndStartServer(2730)
        val pboName = "isolation-test.pbo"

        try {
            // Upload to A
            uploadPbo(serverA, pboName)
                .andExpect(status().isCreated)

            // A sees it
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/server/{id}/scenarios", serverA)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.scenarios[*].name", hasItem(pboName)))

            // B does not see it
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/server/{id}/scenarios", serverB)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.scenarios[*].name", not(hasItem(pboName))))

            // Delete from A
            mockMvc.perform(
                MockMvcRequestBuilders.delete("/api/server/{id}/scenarios/{name}", serverA, pboName)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isNoContent)

            // A no longer sees it
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/server/{id}/scenarios", serverA)
                    .header("Authorization", auth.getToken())
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.scenarios[*].name", not(hasItem(pboName))))
        } finally {
            stopAndDeleteServer(serverA)
            stopAndDeleteServer(serverB)
        }
    }

    @Test
    fun `scenario nonExistentServer returns 404`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/server/{id}/scenarios", 999999L)
                .header("Authorization", auth.getToken())
        )
            .andExpect(status().isNotFound)
    }

    private fun createAndStartServer(port: Int): Int {
        fakeProcesses.scriptServerProcess(FakeProcess.stayingAlive())

        val result = api().post("/api/server", Builders.arma3Server("ScenarioTestServer-$port", port))
            .andExpect(status().isCreated)
            .andReturn()

        val id: Int = JsonPath.read(result.response.contentAsString, "$.id")

        api().post("/api/server/$id/start")
            .andExpect(status().isOk)

        return id
    }

    private fun stopAndDeleteServer(serverId: Int) {
        api().post("/api/server/$serverId/stop")
        api().delete("/api/server/$serverId")
    }

    private fun uploadPbo(serverId: Int, pboName: String) =
        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/server/{id}/scenarios", serverId)
                .file(MockMultipartFile("file", pboName, MediaType.APPLICATION_OCTET_STREAM_VALUE, "FAKEPBODATA".toByteArray()))
                .header("Authorization", auth.getToken())
        )
}
