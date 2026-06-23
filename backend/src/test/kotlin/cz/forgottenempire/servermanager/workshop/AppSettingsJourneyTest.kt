package cz.forgottenempire.servermanager.workshop

import com.jayway.jsonpath.JsonPath
import cz.forgottenempire.servermanager.support.AbstractIntegrationTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
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
import java.time.LocalTime

class AppSettingsJourneyTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var repository: AppSettingsRepository

    private val path = "/api/config/settings"

    @BeforeEach
    fun resetSettings() {
        val settings = repository.findById(1L).orElseThrow()
        settings.automaticModUpdateEnabled = true
        settings.automaticModUpdateTime = LocalTime.of(3, 0)
        repository.save(settings)
    }

    @Test
    fun `patch updates settings`() {
        val json = """{"automaticModUpdateEnabled": true, "automaticModUpdateTime": "05:30"}"""
        api().patch(path, json)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.automaticModUpdateEnabled").value(true))
            .andExpect(jsonPath("$.automaticModUpdateTime").value("05:30"))
    }

    @Test
    fun `get after patch returns updated values`() {
        val updateJson = """{"automaticModUpdateEnabled": true, "automaticModUpdateTime": "04:15"}"""
        api().patch(path, updateJson)
            .andExpect(status().isOk)

        api().get(path)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.automaticModUpdateEnabled").value(true))
            .andExpect(jsonPath("$.automaticModUpdateTime").value("04:15"))
    }

    @Test
    fun `patch disables auto update`() {
        val json = """{"automaticModUpdateEnabled": false, "automaticModUpdateTime": "03:00"}"""
        api().patch(path, json)
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.automaticModUpdateEnabled").value(false))
    }

    @Test
    fun `patch with enabled true and missing time returns 400`() {
        val json = """{"automaticModUpdateEnabled": true}"""
        api().patch(path, json)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").isString)
    }

    @Test
    fun `patch with invalid time format returns 400`() {
        val json = """{"automaticModUpdateEnabled": true, "automaticModUpdateTime": "25:99"}"""
        api().patch(path, json)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").isString)
    }

    @Test
    fun `patch without enabled field returns 400`() {
        val json = """{"automaticModUpdateTime": "03:00"}"""
        api().patch(path, json)
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.message").isString)
    }

    @Test
    fun `unauthenticated request returns 401 or 302`() {
        mockMvc.perform(
            MockMvcRequestBuilders.get(path)
                .with(csrf())
        ).andExpect { result ->
            assertThat(result.response.status).isIn(302, 401)
        }
    }

    @Test
    fun `user without MANAGE_APP_SETTINGS is forbidden`() {
        val json = """{"automaticModUpdateEnabled": true, "automaticModUpdateTime": "05:00"}"""
        mockMvc.perform(
            MockMvcRequestBuilders.patch(path)
                .with(csrf())
                .with(user("viewer").authorities(
                    SimpleGrantedAuthority(PermissionCode.SERVER_VIEW)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        ).andExpect(status().isForbidden)
    }

    @Test
    fun `scheduler respects disabled setting`() {
        val json = """{"automaticModUpdateEnabled": false, "automaticModUpdateTime": "03:00"}"""
        val result = api().patch(path, json)
            .andExpect(status().isOk)
            .andReturn()

        val response = result.response.contentAsString
        val enabled: Boolean = JsonPath.read(response, "$.automaticModUpdateEnabled")
        assertThat(enabled).isFalse()
    }
}
