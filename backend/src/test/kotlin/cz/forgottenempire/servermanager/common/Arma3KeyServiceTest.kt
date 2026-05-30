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

    private lateinit var pathsFactory: PathsFactory
    private lateinit var arma3InstancePaths: Arma3InstancePaths
    private lateinit var arma3KeyService: Arma3KeyService

    @BeforeEach
    fun setUp() {
        pathsFactory = PathsFactory(
            tempDir.toString(),
            tempDir.resolve("mods").toString(),
            tempDir.resolve("logs").toString(),
            "/steamcmd/steamcmd",
            "/steamcmd/cache.json"
        )
        arma3InstancePaths = Arma3InstancePaths(pathsFactory)
        arma3KeyService = Arma3KeyService(pathsFactory, arma3InstancePaths)

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
    fun rebuildInstanceBikeys_populatesActiveModBikeys() {
        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = true, includeLocal = true))

        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(SERVER_ID)
        assertThat(instanceKeys).isDirectory()
        assertThat(instanceKeys.resolve("mod.bikey")).exists()
        assertThat(instanceKeys.resolve("local.bikey")).exists()
    }

    @Test
    fun rebuildInstanceBikeys_containsOnlyActiveMods_notAllInstalled() {
        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = false, includeLocal = false))

        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(SERVER_ID)
        assertThat(instanceKeys.resolve("mod.bikey")).doesNotExist()
        assertThat(instanceKeys.resolve("local.bikey")).doesNotExist()
    }

    @Test
    fun rebuildInstanceKeys_secondCall_clearsPreviousBikeys() {
        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = true, includeLocal = false))
        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = false, includeLocal = false))

        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(SERVER_ID)
        assertThat(instanceKeys.resolve("mod.bikey")).doesNotExist()
    }

    @Test
    fun rebuildInstanceBikeys_mergesCustomBikeys() {
        val customDir = arma3InstancePaths.getInstanceCustomBikeysPath(SERVER_ID)
        Files.createDirectories(customDir)
        Files.createFile(customDir.resolve("community.bikey"))

        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = false, includeLocal = false))

        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(SERVER_ID)
        assertThat(instanceKeys.resolve("community.bikey")).exists()
    }

    @Test
    fun rebuildInstanceBikeys_customBikeysSurviveSecondRebuild() {
        val customDir = arma3InstancePaths.getInstanceCustomBikeysPath(SERVER_ID)
        Files.createDirectories(customDir)
        Files.createFile(customDir.resolve("community.bikey"))

        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = false, includeLocal = false))
        arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = false, includeLocal = false))

        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(SERVER_ID)
        assertThat(instanceKeys.resolve("community.bikey")).exists()
    }

    @Test
    fun rebuildInstanceBikeys_whenCustomBikeysAbsent_doesNotThrow() {
        assertThatNoException().isThrownBy {
            arma3KeyService.rebuildInstanceBikeys(serverWith(includeWorkshop = false, includeLocal = false))
        }
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

    @Test
    fun listProvidedKeys_workshopMod_returnsKeyTaggedWithModName() {
        val mod = WorkshopMod(WORKSHOP_MOD_ID)
        mod.name = "ACE3"
        val entry = Arma3ServerActiveMod()
        entry.mod = mod
        val server = serverWith(includeWorkshop = false, includeLocal = false)
        server.activeMods = listOf(entry)

        val result = arma3KeyService.listProvidedKeys(server)

        assertThat(result).containsExactly(ProvidedKeyInfo("mod.bikey", "ACE3"))
    }

    @Test
    fun listProvidedKeys_workshopModWithoutName_fallsBackToModId() {
        val entry = Arma3ServerActiveMod()
        entry.mod = WorkshopMod(WORKSHOP_MOD_ID) // name is null
        val server = serverWith(includeWorkshop = false, includeLocal = false)
        server.activeMods = listOf(entry)

        val result = arma3KeyService.listProvidedKeys(server)

        assertThat(result).containsExactly(ProvidedKeyInfo("mod.bikey", WORKSHOP_MOD_ID.toString()))
    }

    @Test
    fun listProvidedKeys_localMod_returnsKeyTaggedWithModName() {
        val result = arma3KeyService.listProvidedKeys(serverWith(includeWorkshop = false, includeLocal = true))

        assertThat(result).containsExactly(ProvidedKeyInfo("local.bikey", LOCAL_MOD_NAME))
    }

    @Test
    fun listProvidedKeys_baseGameDir_returnsKeyTaggedWithBaseGameSource() {
        val baseGameKeys = pathsFactory.getServerKeysPath(ServerType.ARMA3)
        Files.createDirectories(baseGameKeys)
        Files.createFile(baseGameKeys.resolve("game.bikey"))

        val result = arma3KeyService.listProvidedKeys(serverWith(includeWorkshop = false, includeLocal = false))

        assertThat(result).containsExactly(ProvidedKeyInfo("game.bikey", ProvidedKeyInfo.BASE_GAME_SOURCE))
    }

    @Test
    fun listProvidedKeys_allSources_returnsAllKeys() {
        val baseGameKeys = pathsFactory.getServerKeysPath(ServerType.ARMA3)
        Files.createDirectories(baseGameKeys)
        Files.createFile(baseGameKeys.resolve("game.bikey"))

        val result = arma3KeyService.listProvidedKeys(serverWith(includeWorkshop = true, includeLocal = true))

        assertThat(result).extracting("name")
            .containsExactlyInAnyOrder("mod.bikey", "local.bikey", "game.bikey")
    }

    @Test
    fun listProvidedKeys_noActiveMods_noBaseGameDir_returnsEmpty() {
        val result = arma3KeyService.listProvidedKeys(serverWith(includeWorkshop = false, includeLocal = false))

        assertThat(result).isEmpty()
    }

    @Test
    fun listProvidedKeys_modStoreMissing_doesNotThrow() {
        val entry = Arma3ServerActiveMod()
        entry.mod = WorkshopMod(9999L) // no dir seeded for this id
        val server = serverWith(includeWorkshop = false, includeLocal = false)
        server.activeMods = listOf(entry)

        assertThatNoException().isThrownBy { arma3KeyService.listProvidedKeys(server) }
    }
}
