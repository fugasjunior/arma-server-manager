package cz.forgottenempire.servermanager.journey

import com.jayway.jsonpath.JsonPath
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest
import cz.forgottenempire.servermanager.support.dsl.Builders
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * Journey tests for per-instance bikey endpoints.
 * Endpoints: GET/POST /api/server/{id}/keys and DELETE /api/server/{id}/keys/{name}.
 *
 * Unlike scenarios, bikey endpoints do not require the server to be started —
 * upload creates the custom_bikeys/ dir on demand.
 */
class BiKeyJourneyTest : AbstractIntegrationTest() {

    @Test
    fun `bikey uploadListDelete happyPath`() {
        val serverId = createServer(2800)
        try {
            val keyName = "community.bikey"

            // Upload → 201 with updated list in body
            uploadBikey(serverId, keyName)
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.keys[*].name", hasItem(keyName)))

            // List
            api().get("/api/server/$serverId/keys")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.keys[*].name", hasItem(keyName)))

            // Delete
            api().delete("/api/server/$serverId/keys/$keyName")
                .andExpect(status().isNoContent)

            // Confirm gone
            api().get("/api/server/$serverId/keys")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.keys[*].name", not(hasItem(keyName))))
        } finally {
            api().delete("/api/server/$serverId")
        }
    }

    @Test
    fun `bikey crossInstanceIsolation`() {
        val serverA = createServer(2810)
        val serverB = createServer(2820)
        val keyName = "isolation-test.bikey"

        try {
            // Upload to A
            uploadBikey(serverA, keyName).andExpect(status().isCreated)

            // A sees it
            api().get("/api/server/$serverA/keys")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.keys[*].name", hasItem(keyName)))

            // B does not see it
            api().get("/api/server/$serverB/keys")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.keys[*].name", not(hasItem(keyName))))
        } finally {
            api().delete("/api/server/$serverA")
            api().delete("/api/server/$serverB")
        }
    }

    @Test
    fun `bikey emptyList whenNoBikeysUploaded`() {
        val serverId = createServer(2830)
        try {
            api().get("/api/server/$serverId/keys")
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.keys").isEmpty)
        } finally {
            api().delete("/api/server/$serverId")
        }
    }

    @Test
    fun `bikey invalidFile returns 400`() {
        val serverId = createServer(2840)
        try {
            // Bytes without RSA1 marker → BiKeyValidator rejects → 400
            api().multipartPost(
                "/api/server/$serverId/keys",
                "file",
                "invalid.bikey",
                "NOT_A_REAL_BIKEY".toByteArray(),
                MediaType.APPLICATION_OCTET_STREAM_VALUE
            )
                .andExpect(status().isBadRequest)
        } finally {
            api().delete("/api/server/$serverId")
        }
    }

    @Test
    fun `bikey nonExistentServer returns 404`() {
        api().get("/api/server/999999/keys")
            .andExpect(status().isNotFound)
    }

    private fun createServer(port: Int): Int {
        val result = api().post("/api/server", Builders.arma3Server("BiKeyTestServer-$port", port))
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
