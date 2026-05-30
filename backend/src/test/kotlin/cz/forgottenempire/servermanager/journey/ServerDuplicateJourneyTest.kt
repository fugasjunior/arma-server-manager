package cz.forgottenempire.servermanager.journey

import com.jayway.jsonpath.JsonPath
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest
import cz.forgottenempire.servermanager.support.dsl.Builders
import org.hamcrest.Matchers.containsString
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Journey test for POST /api/server/{id}/duplicate.
 *
 * Verifies that:
 * - The cloned server is persisted with name "<source> (copy)" and a new id.
 * - Per-instance filesystem assets (custom bikeys) are copied to the new instance's directory.
 *
 * No server process is started — bikey upload does not require a running server.
 */
class ServerDuplicateJourneyTest : AbstractIntegrationTest() {

    @Test
    fun `duplicate creates copy with name suffix and copies bikeys`() {
        val srcId = createServer(2900)
        var copyId: Int? = null
        try {
            val keyName = "community.bikey"
            uploadBikey(srcId, keyName).andExpect(status().isCreated)

            val result = api().post("/api/server/$srcId/duplicate")
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.name", containsString("(copy)")))
                .andReturn()

            copyId = JsonPath.read(result.response.contentAsString, "$.id")

            api().get("/api/server/$copyId/keys")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.keys[*].name", hasItem(keyName)))
        } finally {
            copyId?.let { api().delete("/api/server/$it") }
            api().delete("/api/server/$srcId")
        }
    }

    private fun createServer(port: Int): Int {
        val result = api().post("/api/server", Builders.arma3Server("DupSrc-$port", port))
            .andExpect(status().isCreated)
            .andReturn()
        return JsonPath.read(result.response.contentAsString, "$.id")
    }

    private fun uploadBikey(serverId: Int, keyName: String) =
        api().multipartPost(
            "/api/server/$serverId/keys",
            "file",
            keyName,
            "prefix_RSA1_suffix".toByteArray(),
            MediaType.APPLICATION_OCTET_STREAM_VALUE
        )
}
