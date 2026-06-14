package cz.forgottenempire.servermanager.serverinstance

import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.ServerType
import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException
import cz.forgottenempire.servermanager.serverinstance.entities.DayZServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.MockedStatic
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import java.nio.file.Files
import java.nio.file.Path

@ExtendWith(MockitoExtension::class)
class ConfigOverrideServiceUnitTest {

    @Mock
    private lateinit var pathsFactory: PathsFactory

    @Mock
    private lateinit var freeMarkerConfigurer: FreeMarkerConfigurer

    @Mock
    private lateinit var overrideRepository: ServerConfigOverrideRepository

    private lateinit var service: ConfigOverrideService
    private lateinit var server: DayZServer

    @BeforeEach
    fun setUp() {
        service = ConfigOverrideService(pathsFactory, freeMarkerConfigurer, overrideRepository)
        server = DayZServer().apply {
            type = ServerType.DAYZ
        }
    }

    @AfterEach
    fun tearDown() {
        // static mocks closed in each test via use blocks
    }

    @Test
    fun `when configKey invalid for server type then throw`() {
        val thrown = org.junit.jupiter.api.assertThrows<CustomUserErrorException> {
            service.seedConfigOverride(ConfigFileKey.REFORGER_JSON, null, server)
        }
        assertThat(thrown.message).contains("not valid for server type")
    }

    @Test
    fun `when serverId null then render template`() {
        mockStatic(ServerConfig::class.java).use { configMock ->
            configMock.`when`<String> { ServerConfig.renderToString(any(), anyString(), any()) }
                .thenReturn("rendered template")

            val result = service.seedConfigOverride(ConfigFileKey.DAYZ_SERVER_CFG, null, server)

            assertThat(result.configKey).isEqualTo("DAYZ_SERVER_CFG")
            assertThat(result.content).isEqualTo("rendered template")
            assertThat(server.id).isEqualTo(0L)
        }
    }

    @Test
    fun `when serverId provided and file exists then read file`() {
        `when`(pathsFactory.getConfigFilePath(any(), anyString())).thenReturn(Path.of("/tmp/test.cfg"))
        mockStatic(Files::class.java).use { filesMock ->
            filesMock.`when`<Boolean> { Files.exists(any()) }.thenReturn(true)
            mockStatic(ServerConfig::class.java).use { configMock ->
                configMock.`when`<String> { ServerConfig.readFromFile(any()) }
                    .thenReturn("file content")

                val result = service.seedConfigOverride(ConfigFileKey.DAYZ_SERVER_CFG, 42L, server)

                assertThat(result.configKey).isEqualTo("DAYZ_SERVER_CFG")
                assertThat(result.content).isEqualTo("file content")
            }
        }
    }

    @Test
    fun `when serverId provided but file missing then render template`() {
        `when`(pathsFactory.getConfigFilePath(any(), anyString())).thenReturn(Path.of("/tmp/test.cfg"))
        mockStatic(Files::class.java).use { filesMock ->
            filesMock.`when`<Boolean> { Files.exists(any()) }.thenReturn(false)
            mockStatic(ServerConfig::class.java).use { configMock ->
                configMock.`when`<String> { ServerConfig.renderToString(any(), anyString(), any()) }
                    .thenReturn("rendered fallback")

                val result = service.seedConfigOverride(ConfigFileKey.DAYZ_SERVER_CFG, 42L, server)

                assertThat(result.configKey).isEqualTo("DAYZ_SERVER_CFG")
                assertThat(result.content).isEqualTo("rendered fallback")
                assertThat(server.id).isEqualTo(42L)
            }
        }
    }

    @Test
    fun `when file read throws then fallback to template render`() {
        `when`(pathsFactory.getConfigFilePath(any(), anyString())).thenReturn(Path.of("/tmp/test.cfg"))
        mockStatic(Files::class.java).use { filesMock ->
            filesMock.`when`<Boolean> { Files.exists(any()) }.thenReturn(true)
            mockStatic(ServerConfig::class.java).use { configMock ->
                configMock.`when`<String> { ServerConfig.readFromFile(any()) }
                    .thenThrow(java.io.IOException("read error"))
                configMock.`when`<String> { ServerConfig.renderToString(any(), anyString(), any()) }
                    .thenReturn("fallback after io error")

                val result = service.seedConfigOverride(ConfigFileKey.DAYZ_SERVER_CFG, 42L, server)

                assertThat(result.configKey).isEqualTo("DAYZ_SERVER_CFG")
                assertThat(result.content).isEqualTo("fallback after io error")
                assertThat(server.id).isEqualTo(42L)
            }
        }
    }

    @Test
    fun `when serverId null then id set to zero`() {
        mockStatic(ServerConfig::class.java).use { configMock ->
            configMock.`when`<String> { ServerConfig.renderToString(any(), anyString(), any()) }
                .thenReturn("any")

            service.seedConfigOverride(ConfigFileKey.DAYZ_SERVER_CFG, null, server)

            assertThat(server.id).isEqualTo(0L)
        }
    }

    @Test
    fun `when serverId provided then id set to serverId`() {
        `when`(pathsFactory.getConfigFilePath(any(), anyString())).thenReturn(Path.of("/tmp/test.cfg"))
        mockStatic(Files::class.java).use { filesMock ->
            filesMock.`when`<Boolean> { Files.exists(any()) }.thenReturn(false)
            mockStatic(ServerConfig::class.java).use { configMock ->
                configMock.`when`<String> { ServerConfig.renderToString(any(), anyString(), any()) }
                    .thenReturn("any")

                service.seedConfigOverride(ConfigFileKey.DAYZ_SERVER_CFG, 99L, server)

                assertThat(server.id).isEqualTo(99L)
            }
        }
    }
}
