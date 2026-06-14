package cz.forgottenempire.servermanager.serverinstance

import com.jayway.jsonpath.JsonPath
import cz.forgottenempire.servermanager.api.model.Arma3ServerDto
import cz.forgottenempire.servermanager.api.model.ConfigOverrideDto
import cz.forgottenempire.servermanager.api.model.DayZServerDto
import cz.forgottenempire.servermanager.api.model.ReforgerServerDto
import cz.forgottenempire.servermanager.api.model.SeedConfigOverrideRequest
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.security.core.authority.SimpleGrantedAuthority
import cz.forgottenempire.servermanager.security.permission.PermissionCode
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.io.File
import java.nio.file.Files

class ServerConfigOverrideJourneyTest : AbstractIntegrationTest() {

    private fun dayZServer(name: String, port: Int): DayZServerDto {
        return DayZServerDto()
            .type("DAYZ")
            .name(name)
            .port(port)
            .queryPort(port + 1)
            .maxPlayers(20)
            .timeAcceleration(1.0)
            .nightTimeAcceleration(1.0)
            .activeMods(listOf())
            .activeLocalMods(listOf())
            .customLaunchParameters(listOf())
    }

    private fun getConfigFile(serverId: Long): File {
        val base = System.getProperty("java.io.tmpdir") + "/arma-server-manager-test/servers"
        return File("$base/DAYZ/DAYZ_$serverId.cfg")
    }

    @Autowired
    private lateinit var overrideRepository: ServerConfigOverrideRepository

    @Test
    fun `create server with override persists row and writes raw text`() {
        val dto = dayZServer("DayzOverrideTest", 6302)
            .configOverrides(listOf(
                ConfigOverrideDto()
                    .configKey("DAYZ_SERVER_CFG")
                    .content("hostname = Override Test; password = secret123;")
            ))

        val result = api().post("/api/server", dto)
            .andExpect(status().isCreated)
            .andReturn()

        val serverId: Int = JsonPath.read(result.response.contentAsString, "$.id")

        val persisted = overrideRepository.findByServerIdAndConfigKey(serverId.toLong(), ConfigFileKey.DAYZ_SERVER_CFG)
        assertThat(persisted).isNotNull
        assertThat(persisted!!.content).isEqualTo("hostname = Override Test; password = secret123;")

        val configFile = getConfigFile(serverId.toLong())
        assertThat(configFile).exists()
        assertThat(Files.readString(configFile.toPath())).isEqualTo("hostname = Override Test; password = secret123;")
    }

    @Test
    fun `update override content changes file content`() {
        val createDto = dayZServer("DayzUpdateTest", 7302)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("DAYZ_SERVER_CFG").content("original content")
            ))
        val createResult = api().post("/api/server", createDto)
            .andExpect(status().isCreated)
            .andReturn()
        val serverId: Int = JsonPath.read(createResult.response.contentAsString, "$.id")

        val updateDto = dayZServer("DayzUpdateTest", 7302)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("DAYZ_SERVER_CFG").content("updated content")
            ))
        val json = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(updateDto)
        api().put("/api/server/$serverId", json)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.configOverrides[0].content").value("updated content"))

        val configFile = getConfigFile(serverId.toLong())
        assertThat(Files.readString(configFile.toPath())).isEqualTo("updated content")
    }

    @Test
    fun `remove override reverts to template generated config`() {
        val createDto = dayZServer("DayzRevertTest", 8302)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("DAYZ_SERVER_CFG").content("raw override text")
            ))
        val createResult = api().post("/api/server", createDto)
            .andExpect(status().isCreated)
            .andReturn()
        val serverId: Int = JsonPath.read(createResult.response.contentAsString, "$.id")

        val configFile = getConfigFile(serverId.toLong())
        assertThat(Files.readString(configFile.toPath())).isEqualTo("raw override text")

        val updateDto = dayZServer("DayzRevertTest", 8302)
            .configOverrides(listOf())
        val json = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(updateDto)
        api().put("/api/server/$serverId", json)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.configOverrides").doesNotExist())

        val persisted = overrideRepository.findByServerIdAndConfigKey(serverId.toLong(), ConfigFileKey.DAYZ_SERVER_CFG)
        assertThat(persisted).isNull()

        assertThat(configFile).exists()
        val regenerated = Files.readString(configFile.toPath())
        assertThat(regenerated).contains("hostname")
        assertThat(regenerated).doesNotContain("raw override text")
    }

    @Test
    fun `configOverrides appears in response body`() {
        val dto = dayZServer("DayzResponseTest", 10302)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("DAYZ_SERVER_CFG").content("verify response")
            ))
        api().post("/api/server", dto)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.configOverrides").isArray)
            .andExpect(jsonPath("$.configOverrides[0].advanced").value(true))
    }

    @Test
    fun `seed returns template render for draft with no id`() {
        val draft = dayZServer("SeedDraftTest", 5402)
        val request = SeedConfigOverrideRequest()
            .configKey("DAYZ_SERVER_CFG")
            .server(draft)

        api().post("/api/server/config-overrides/seed", request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.configKey").value("DAYZ_SERVER_CFG"))
            .andExpect(jsonPath("$.content").isString)
            .andExpect(jsonPath("$.content").isNotEmpty)
    }

    @Test
    fun `seed returns on-disk content for existing server`() {
        val createDto = dayZServer("SeedOnDiskTest", 6402)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("DAYZ_SERVER_CFG").content("on-disk content")
            ))
        val createResult = api().post("/api/server", createDto)
            .andExpect(status().isCreated)
            .andReturn()
        val serverId: Int = JsonPath.read(createResult.response.contentAsString, "$.id")

        val configFile = getConfigFile(serverId.toLong())
        assertThat(Files.readString(configFile.toPath())).isEqualTo("on-disk content")

        val draft = dayZServer("SeedOnDiskTest", 6402).id(serverId.toLong())
        val request = SeedConfigOverrideRequest()
            .configKey("DAYZ_SERVER_CFG")
            .server(draft)
        api().post("/api/server/config-overrides/seed", request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.configKey").value("DAYZ_SERVER_CFG"))
            .andExpect(jsonPath("$.content").value("on-disk content"))
    }

    @Test
    fun `user without secrets permission can update server that has overrides without losing them`() {
        // Created by a fully authorized admin with a real override
        val createDto = dayZServer("ModifierOverrideTest", 9302)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("DAYZ_SERVER_CFG").content("secret override")
            ))
        val createResult = api().post("/api/server", createDto)
            .andExpect(status().isCreated)
            .andReturn()
        val serverId: Int = JsonPath.read(createResult.response.contentAsString, "$.id")

        // A user lacking SERVER_SECRETS_VIEW edits a non-override field; the override
        // round-trips back masked (content null), as it would from the UI.
        val updateDto = dayZServer("ModifierOverrideTestRenamed", 9302)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("DAYZ_SERVER_CFG").advanced(true)
            ))
        val json = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(updateDto)
        mockMvc.perform(
            MockMvcRequestBuilders.put("/api/server/$serverId")
                .with(csrf())
                .with(user("modifier").authorities(
                    SimpleGrantedAuthority(PermissionCode.SERVER_MODIFY),
                    SimpleGrantedAuthority(PermissionCode.SERVER_VIEW)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isOk)

        // Override content is preserved in DB and on disk
        val persisted = overrideRepository.findByServerIdAndConfigKey(serverId.toLong(), ConfigFileKey.DAYZ_SERVER_CFG)
        assertThat(persisted).isNotNull
        assertThat(persisted!!.content).isEqualTo("secret override")
        assertThat(Files.readString(getConfigFile(serverId.toLong()).toPath())).isEqualTo("secret override")
    }

    @Test
    fun `seed endpoint rejects unauthenticated requests`() {
        val draft = dayZServer("SeedNoAuthTest", 7402)
        val request = SeedConfigOverrideRequest()
            .configKey("DAYZ_SERVER_CFG")
            .server(draft)
        val json = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/server/config-overrides/seed")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect { result ->
            assertThat(result.response.status).isIn(302, 401, 403)
        }
    }

    // --- Reforger-specific tests ---

    private fun reforgerServer(name: String, port: Int): ReforgerServerDto {
        return ReforgerServerDto()
            .type("REFORGER")
            .name(name)
            .port(port)
            .queryPort(port + 1)
            .maxPlayers(16)
            .scenarioId("{ECC61978EDCC2B5A}Missions/23_Campaign.conf")
            .activeMods(listOf())
            .customLaunchParameters(listOf())
    }

    private fun getReforgerConfigFile(serverId: Long): File {
        val base = System.getProperty("java.io.tmpdir") + "/arma-server-manager-test/servers"
        return File("$base/REFORGER/REFORGER_$serverId.json")
    }

    @Test
    fun `create Reforger server with valid JSON override succeeds`() {
        val validJson = """
        {
            "bindAddress": "",
            "bindPort": 2001,
            "publicAddress": "",
            "publicPort": 2001,
            "game": {
                "name": "ReforgerTest",
                "scenarioId": "{ECC61978EDCC2B5A}Missions/23_Campaign.conf",
                "maxPlayers": 16
            },
            "a2s": {
                "port": 17777
            }
        }
        """.trimIndent()
        val dto = reforgerServer("ReforgerJsonTest", 2001)
            .configOverrides(listOf(
                ConfigOverrideDto()
                    .configKey("REFORGER_JSON")
                    .content(validJson)
            ))

        val result = api().post("/api/server", dto)
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.configOverrides[0].configKey").value("REFORGER_JSON"))
            .andExpect(jsonPath("$.configOverrides[0].content").value(validJson))
            .andReturn()

        val serverId: Int = JsonPath.read(result.response.contentAsString, "$.id")
        val configFile = getReforgerConfigFile(serverId.toLong())
        assertThat(configFile).exists()
        assertThat(Files.readString(configFile.toPath())).isEqualTo(validJson)
    }

    @Test
    fun `create Reforger server with invalid JSON override returns 400`() {
        val invalidJson = "{ missing quote: value }"
        val dto = reforgerServer("ReforgerBadJsonTest", 3001)
            .configOverrides(listOf(
                ConfigOverrideDto()
                    .configKey("REFORGER_JSON")
                    .content(invalidJson)
            ))

        api().post("/api/server", dto)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(containsString("Invalid JSON")))
            .andExpect(jsonPath("$.message").value(containsString("line")))
            .andExpect(jsonPath("$.message").value(containsString("column")))
    }

    @Test
    fun `update Reforger override with invalid JSON returns 400`() {
        val validJson = "{\"bindAddress\": \"\", \"bindPort\": 2001}"
        val createDto = reforgerServer("ReforgerUpdateTest", 4001)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("REFORGER_JSON").content(validJson)
            ))
        val createResult = api().post("/api/server", createDto)
            .andExpect(status().isCreated)
            .andReturn()
        val serverId: Int = JsonPath.read(createResult.response.contentAsString, "$.id")

        val invalidJson = "{ bad key: 123 }"
        val updateDto = reforgerServer("ReforgerUpdateTest", 4001)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("REFORGER_JSON").content(invalidJson)
            ))
        val json = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(updateDto)
        api().put("/api/server/$serverId", json)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(containsString("line")))
    }

    @Test
    fun `remove Reforger JSON override reverts to template`() {
        val validJson = "{\"override\": true}"
        val createDto = reforgerServer("ReforgerRevertTest", 5001)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("REFORGER_JSON").content(validJson)
            ))
        val createResult = api().post("/api/server", createDto)
            .andExpect(status().isCreated)
            .andReturn()
        val serverId: Int = JsonPath.read(createResult.response.contentAsString, "$.id")

        val configFile = getReforgerConfigFile(serverId.toLong())
        assertThat(Files.readString(configFile.toPath())).isEqualTo(validJson)

        val updateDto = reforgerServer("ReforgerRevertTest", 5001)
            .configOverrides(listOf())
        val json = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(updateDto)
        api().put("/api/server/$serverId", json)
            .andExpect(status().isOk)

        assertThat(configFile).exists()
        val regenerated = Files.readString(configFile.toPath())
        assertThat(regenerated).contains("\"bindAddress\"")
        assertThat(regenerated).doesNotContain("\"override\"")
    }

    @Test
    fun `seed Reforger JSON returns rendered content`() {
        val draft = reforgerServer("SeedReforgerTest", 6001)
        val request = SeedConfigOverrideRequest()
            .configKey("REFORGER_JSON")
            .server(draft)

        api().post("/api/server/config-overrides/seed", request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.configKey").value("REFORGER_JSON"))
            .andExpect(jsonPath("$.content").isString)
            .andExpect(jsonPath("$.content").isNotEmpty)
            .andExpect(jsonPath("$.content").value(containsString("\"bindAddress\"")))
    }

    // --- Arma3-specific tests ---

    private fun arma3Server(name: String, port: Int): Arma3ServerDto {
        return Arma3ServerDto()
            .type("ARMA3")
            .name(name)
            .port(port)
            .queryPort(port + 1)
            .maxPlayers(16)
            .activeMods(listOf())
            .activeLocalMods(listOf())
            .activeDLCs(listOf())
            .customLaunchParameters(listOf())
    }

    private fun getArma3ConfigFile(serverId: Long): File {
        val base = System.getProperty("java.io.tmpdir") + "/arma-server-manager-test/servers"
        return File("$base/ARMA3/ARMA3_$serverId.cfg")
    }

    private fun getArma3ProfileFile(serverId: Long): File {
        val base = System.getProperty("java.io.tmpdir") + "/arma-server-manager-test/profiles"
        // Profile path: profilesDir/home/ARMA3_{id}/ARMA3_{id}.Arma3Profile
        return File("$base/home/ARMA3_$serverId/ARMA3_$serverId.Arma3Profile")
    }

    private fun getArma3NetworkConfigFile(serverId: Long): File {
        val base = System.getProperty("java.io.tmpdir") + "/arma-server-manager-test/servers"
        return File("$base/ARMA3/ARMA3_${serverId}_network.cfg")
    }

    @Test
    fun `create Arma3 server with server cfg override writes raw text`() {
        val dto = arma3Server("Arma3CfgTest", 2302)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("ARMA3_SERVER_CFG").content("hostname = ArmaOverrideTest;")
            ))

        val result = api().post("/api/server", dto)
            .andExpect(status().isCreated)
            .andReturn()

        val serverId: Int = JsonPath.read(result.response.contentAsString, "$.id")

        val persisted = overrideRepository.findByServerIdAndConfigKey(serverId.toLong(), ConfigFileKey.ARMA3_SERVER_CFG)
        assertThat(persisted).isNotNull
        assertThat(persisted!!.content).isEqualTo("hostname = ArmaOverrideTest;")

        val configFile = getArma3ConfigFile(serverId.toLong())
        assertThat(configFile).exists()
        assertThat(Files.readString(configFile.toPath())).isEqualTo("hostname = ArmaOverrideTest;")
    }

    @Test
    fun `remove Arma3 server cfg override reverts to template`() {
        val createDto = arma3Server("Arma3RevertTest", 2402)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("ARMA3_SERVER_CFG").content("raw override")
            ))
        val createResult = api().post("/api/server", createDto)
            .andExpect(status().isCreated)
            .andReturn()
        val serverId: Int = JsonPath.read(createResult.response.contentAsString, "$.id")

        val configFile = getArma3ConfigFile(serverId.toLong())
        assertThat(Files.readString(configFile.toPath())).isEqualTo("raw override")

        val updateDto = arma3Server("Arma3RevertTest", 2402).configOverrides(listOf())
        val json = com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(updateDto)
        api().put("/api/server/$serverId", json)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.configOverrides").doesNotExist())

        assertThat(configFile).exists()
        val regenerated = Files.readString(configFile.toPath())
        assertThat(regenerated).contains("hostname")
        assertThat(regenerated).doesNotContain("raw override")
    }

    @Test
    fun `create Arma3 server with ARMA3_NETWORK_CFG override writes network file without networkSettings`() {
        val dto = arma3Server("Arma3NetworkTest", 2502)
            .configOverrides(listOf(
                ConfigOverrideDto().configKey("ARMA3_NETWORK_CFG").content("MaxMessagesSend = 128;")
            ))

        val result = api().post("/api/server", dto)
            .andExpect(status().isCreated)
            .andReturn()

        val serverId: Int = JsonPath.read(result.response.contentAsString, "$.id")
        val networkFile = getArma3NetworkConfigFile(serverId.toLong())
        assertThat(networkFile).exists()
        assertThat(Files.readString(networkFile.toPath())).isEqualTo("MaxMessagesSend = 128;")
    }

    @Test
    fun `seed renders Arma3 server cfg template for no-id draft`() {
        val draft = arma3Server("Arma3SeedTest", 2602)
        val request = SeedConfigOverrideRequest()
            .configKey("ARMA3_SERVER_CFG")
            .server(draft)

        api().post("/api/server/config-overrides/seed", request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.configKey").value("ARMA3_SERVER_CFG"))
            .andExpect(jsonPath("$.content").isString)
            .andExpect(jsonPath("$.content").isNotEmpty)
    }

    @Test
    fun `seed renders Arma3 profile template for no-id draft with null difficultySettings`() {
        val draft = arma3Server("Arma3ProfileSeedTest", 2702)
        val request = SeedConfigOverrideRequest()
            .configKey("ARMA3_PROFILE")
            .server(draft)

        api().post("/api/server/config-overrides/seed", request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.configKey").value("ARMA3_PROFILE"))
            .andExpect(jsonPath("$.content").isString)
    }

    @Test
    fun `seed renders Arma3 network cfg template for no-id draft with null networkSettings`() {
        val draft = arma3Server("Arma3NetworkSeedTest", 2802)
        val request = SeedConfigOverrideRequest()
            .configKey("ARMA3_NETWORK_CFG")
            .server(draft)

        api().post("/api/server/config-overrides/seed", request)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.configKey").value("ARMA3_NETWORK_CFG"))
            .andExpect(jsonPath("$.content").isString)
    }

    @Test
    fun `JSON validation error includes line and column detail`() {
        val invalidJson = """
        {
            "key": "value"
            "missingComma": true
        }
        """.trimIndent()
        val dto = reforgerServer("ReforgerDetailTest", 8001)
            .configOverrides(listOf(
                ConfigOverrideDto()
                    .configKey("REFORGER_JSON")
                    .content(invalidJson)
            ))

        val response = api().post("/api/server", dto)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").value(containsString("line")))
            .andExpect(jsonPath("$.message").value(containsString("column")))
            .andReturn()

        val message: String = JsonPath.read(response.response.contentAsString, "$.message")
        assertThat(message).contains("line 3")
    }
}
