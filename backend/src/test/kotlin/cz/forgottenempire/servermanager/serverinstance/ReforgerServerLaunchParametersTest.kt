package cz.forgottenempire.servermanager.serverinstance

import cz.forgottenempire.servermanager.common.Arma3InstancePaths
import cz.forgottenempire.servermanager.common.Arma3KeyService
import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.serverinstance.entities.LaunchParameter
import cz.forgottenempire.servermanager.serverinstance.entities.ReforgerServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
import java.nio.file.Path

class ReforgerServerLaunchParametersTest {

    private fun mockLaunchContext(): ServerLaunchContext {
        val pathsFactory = mock(PathsFactory::class.java)
        `when`(pathsFactory.getConfigFilePath(any(), any()))
            .thenReturn(Path.of("/servers/REFORGER/REFORGER_1.json"))
        `when`(pathsFactory.getReforgerProfilePath(anyLong()))
            .thenReturn(Path.of("/servers/REFORGER/profiles/1"))
        `when`(pathsFactory.getReforgerAddonDownloadPath())
            .thenReturn(Path.of("/mods/reforger"))
        return ServerLaunchContext(
            pathsFactory,
            mock(Arma3InstancePaths::class.java),
            mock(Arma3KeyService::class.java),
            mock(FreeMarkerConfigurer::class.java)
        )
    }

    private fun server(): ReforgerServer = ReforgerServer().apply {
        id = 1L
        scenarioId = "{ECC61978EDCC2B5A}Missions/23_Campaign.conf"
    }

    @Test
    fun `getLaunchParameters contains config, profile, addonDownloadDir, logAppend`() {
        val params = server().getLaunchParameters(mockLaunchContext())

        assertThat(params).contains("-config", "-profile", "-addonDownloadDir", "-logAppend")
    }

    @Test
    fun `getLaunchParameters profile is per-instance`() {
        val params = server().getLaunchParameters(mockLaunchContext())

        val profileIdx = params.indexOf("-profile")
        assertThat(profileIdx).isGreaterThan(-1)
        assertThat(params[profileIdx + 1]).contains("profiles/1")
    }

    @Test
    fun `getLaunchParameters addonDownloadDir points to mods base`() {
        val params = server().getLaunchParameters(mockLaunchContext())

        val addonIdx = params.indexOf("-addonDownloadDir")
        assertThat(addonIdx).isGreaterThan(-1)
        assertThat(params[addonIdx + 1]).contains("reforger")
    }

    @Test
    fun `getLaunchParameters does not contain backendlog`() {
        val params = server().getLaunchParameters(mockLaunchContext())

        assertThat(params).doesNotContain("-backendlog")
    }

    @Test
    fun `getLaunchParameters does not hardcode maxFPS`() {
        val params = server().getLaunchParameters(mockLaunchContext())

        assertThat(params).doesNotContain("-maxFPS")
    }

    @Test
    fun `getLaunchParameters custom maxFPS param renders correctly`() {
        val server = server()
        val maxFps = LaunchParameter().apply {
            this.server = server
            name = "maxFPS"
            value = "60"
        }
        server.customLaunchParameters = listOf(maxFps)

        val params = server.getLaunchParameters(mockLaunchContext())

        val idx = params.indexOf("-maxFPS")
        assertThat(idx).isGreaterThan(-1)
        assertThat(params[idx + 1]).isEqualTo("60")
    }
}
