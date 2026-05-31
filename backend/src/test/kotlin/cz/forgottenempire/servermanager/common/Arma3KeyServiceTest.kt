package cz.forgottenempire.servermanager.common

import cz.forgottenempire.servermanager.localmod.LocalMod
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3ServerActiveMod
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3ServerActiveLocalMod
import cz.forgottenempire.servermanager.workshop.WorkshopMod
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatNoException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

private const val SERVER_ID = 1L
private const val WORKSHOP_MOD_ID = 100L
private const val LOCAL_MOD_NAME = "myLocalMod"

class Arma3KeyServiceTest {

    @TempDir
    private lateinit var tempDir: Path

    private lateinit var arma3InstancePaths: Arma3InstancePaths
    private lateinit var arma3KeyService: Arma3KeyService

    @BeforeEach
    fun setUp() {
        val pathsFactory = PathsFactory(
            tempDir.toString(),
            tempDir.resolve("mods").toString(),
            tempDir.resolve("logs").toString(),
            "/steamcmd/steamcmd",
            "/steamcmd/cache.json"
        )
        arma3InstancePaths = Arma3InstancePaths(pathsFactory)
        arma3KeyService = Arma3KeyService(pathsFactory, arma3InstancePaths)

        val gameKeys = pathsFactory.getServerKeysPath(ServerType.ARMA3)
        Files.createDirectories(gameKeys)
        Files.createFile(gameKeys.resolve("a3.bikey"))

        val modStore = pathsFactory.getModInstallationPath(WORKSHOP_MOD_ID, ServerType.ARMA3)
        Files.createDirectories(modStore)
        Files.createFile(modStore.resolve("mod.bikey"))

        val localStore = pathsFactory.getLocalModPath(LOCAL_MOD_NAME, ServerType.ARMA3)
        Files.createDirectories(localStore)
        Files.createFile(localStore.resolve("local.bikey"))
    }

    private fun serverWith(includeWorkshop: Boolean, includeLocal: Boolean): Arma3Server {
        val server = Arma3Server()
        server.id = SERVER_ID
        server.activeDLCs = listOf()

        server.activeMods = if (includeWorkshop) {
            val entry = Arma3ServerActiveMod()
            entry.mod = WorkshopMod(WORKSHOP_MOD_ID)
            listOf(entry)
        } else listOf()

        server.activeLocalMods = if (includeLocal) {
            val localMod = LocalMod()
            localMod.name = LOCAL_MOD_NAME
            val entry = Arma3ServerActiveLocalMod()
            entry.mod = localMod
            listOf(entry)
        } else listOf()

        return server
    }

    @Test
    fun rebuildInstanceBikeys_populatesGameAndActiveModBikeys() {
        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = true, includeLocal = true))

        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(SERVER_ID)
        assertThat(instanceKeys).isDirectory()
        assertThat(instanceKeys.resolve("a3.bikey")).exists()
        assertThat(instanceKeys.resolve("mod.bikey")).exists()
        assertThat(instanceKeys.resolve("local.bikey")).exists()
    }

    @Test
    fun rebuildInstanceBikeys_containsOnlyActiveMods_notAllInstalled() {
        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = false, includeLocal = false))

        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(SERVER_ID)
        assertThat(instanceKeys.resolve("a3.bikey")).exists()
        assertThat(instanceKeys.resolve("mod.bikey")).doesNotExist()
        assertThat(instanceKeys.resolve("local.bikey")).doesNotExist()
    }

    @Test
    fun rebuildInstanceKeys_secondCall_clearsPreviousBikeys() {
        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = true, includeLocal = false))
        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = false, includeLocal = false))

        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(SERVER_ID)
        assertThat(instanceKeys.resolve("mod.bikey")).doesNotExist()
        assertThat(instanceKeys.resolve("a3.bikey")).exists()
    }

    @Test
    fun rebuildInstanceBikeys_whenModStoreMissing_doesNotThrow() {
        val server = Arma3Server()
        server.id = SERVER_ID
        server.activeDLCs = listOf()
        server.activeLocalMods = listOf()
        val entry = Arma3ServerActiveMod()
        entry.mod = WorkshopMod(9999L)
        server.activeMods = listOf(entry)

        assertThatNoException().isThrownBy { arma3KeyService.rebuildInstanceBikeys(server) }
    }
}
