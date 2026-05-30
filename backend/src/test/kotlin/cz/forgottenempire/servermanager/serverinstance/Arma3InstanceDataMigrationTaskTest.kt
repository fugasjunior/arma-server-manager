package cz.forgottenempire.servermanager.serverinstance

import cz.forgottenempire.servermanager.common.Arma3InstancePaths
import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.ServerType
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.io.TempDir
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.nio.file.Files
import java.nio.file.Path

@ExtendWith(MockitoExtension::class)
class Arma3InstanceDataMigrationTaskTest {

    @TempDir
    private lateinit var tempDir: Path

    @Mock
    private lateinit var serverRepository: ServerRepository

    private lateinit var pathsFactory: PathsFactory
    private lateinit var arma3InstancePaths: Arma3InstancePaths
    private lateinit var migrationTask: Arma3InstanceDataMigrationTask

    private val arma3Root get() = pathsFactory.getServerPath(ServerType.ARMA3)

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
        migrationTask = Arma3InstanceDataMigrationTask(serverRepository, pathsFactory, arma3InstancePaths)
    }

    private fun arma3ServerWithId(id: Long): Arma3Server {
        val server = Arma3Server()
        server.id = id
        server.activeMods = listOf()
        server.activeLocalMods = listOf()
        server.activeDLCs = listOf()
        return server
    }

    private fun seedLegacyProfileDir(serverId: Long, sub: String = "home") {
        val profileDir = arma3Root.resolve("custom_profiles").resolve(sub).resolve("ARMA3_$serverId")
        Files.createDirectories(profileDir)
        Files.createFile(profileDir.resolve("save.bin"))
    }

    private fun seedSharedScenarios(vararg names: String) {
        val mpmissions = arma3Root.resolve("mpmissions")
        Files.createDirectories(mpmissions)
        names.forEach { Files.createFile(mpmissions.resolve(it)) }
    }

    @Test
    fun `migrates profile dir from custom_profiles to per-instance location`() {
        seedLegacyProfileDir(serverId = 1L)
        `when`(serverRepository.findAll()).thenReturn(listOf(arma3ServerWithId(1L)))

        migrationTask.migrateToPerInstanceDirectories()

        val dest = arma3InstancePaths.getInstanceProfilesPath(1L).resolve("home").resolve("ARMA3_1")
        assertThat(dest.resolve("save.bin")).exists()
        assertThat(arma3Root.resolve("custom_profiles").resolve("home").resolve("ARMA3_1")).doesNotExist()
    }

    @Test
    fun `copies shared scenarios into per-instance mpmissions for each server`() {
        seedSharedScenarios("mission1.pbo", "mission2.pbo")
        `when`(serverRepository.findAll()).thenReturn(
            listOf(arma3ServerWithId(1L), arma3ServerWithId(2L))
        )

        migrationTask.migrateToPerInstanceDirectories()

        for (id in listOf(1L, 2L)) {
            val mpmissions = arma3InstancePaths.getInstanceMpmissionsPath(id)
            assertThat(mpmissions.resolve("mission1.pbo")).exists()
            assertThat(mpmissions.resolve("mission2.pbo")).exists()
        }
    }

    @Test
    fun `creates marker file after successful migration`() {
        Files.createDirectories(arma3Root)
        `when`(serverRepository.findAll()).thenReturn(listOf(arma3ServerWithId(1L)))

        migrationTask.migrateToPerInstanceDirectories()

        assertThat(arma3Root.resolve(".instance-dirs-migrated")).exists()
    }

    @Test
    fun `second run is a no-op when marker file exists`() {
        seedLegacyProfileDir(serverId = 1L)
        seedSharedScenarios("mission.pbo")
        `when`(serverRepository.findAll()).thenReturn(listOf(arma3ServerWithId(1L)))
        migrationTask.migrateToPerInstanceDirectories()

        // Re-seed legacy dir to verify second run does NOT move it again
        seedLegacyProfileDir(serverId = 1L)

        migrationTask.migrateToPerInstanceDirectories()

        // Legacy dir should still exist (not moved a second time)
        assertThat(arma3Root.resolve("custom_profiles").resolve("home").resolve("ARMA3_1")).exists()
    }

    @Test
    fun `skips profile move when destination already exists`() {
        seedLegacyProfileDir(serverId = 1L)
        val dest = arma3InstancePaths.getInstanceProfilesPath(1L).resolve("home").resolve("ARMA3_1")
        Files.createDirectories(dest)
        Files.createFile(dest.resolve("existing.bin"))
        `when`(serverRepository.findAll()).thenReturn(listOf(arma3ServerWithId(1L)))

        migrationTask.migrateToPerInstanceDirectories()

        // Existing dest untouched; legacy src still present (no move attempted)
        assertThat(dest.resolve("existing.bin")).exists()
        assertThat(arma3Root.resolve("custom_profiles").resolve("home").resolve("ARMA3_1")).exists()
    }

    @Test
    fun `does nothing when ARMA3 install directory does not exist`() {
        val noInstall = tempDir.resolve("ARMA3")
        assertThat(noInstall).doesNotExist()

        migrationTask.migrateToPerInstanceDirectories()

        assertThat(noInstall.resolve(".instance-dirs-migrated")).doesNotExist()
    }

    @Test
    fun `skips scenario copy when shared mpmissions dir is absent`() {
        Files.createDirectories(arma3Root)
        `when`(serverRepository.findAll()).thenReturn(listOf(arma3ServerWithId(1L)))

        migrationTask.migrateToPerInstanceDirectories()

        val mpmissions = arma3InstancePaths.getInstanceMpmissionsPath(1L)
        // If the dir was created, it must be empty — no phantom scenarios
        if (mpmissions.toFile().exists()) {
            assertThat(mpmissions.toFile().listFiles()).isEmpty()
        }
    }

    @Test
    fun `checks Users subdir for cross-platform profiles`() {
        seedLegacyProfileDir(serverId = 1L, sub = "Users")
        `when`(serverRepository.findAll()).thenReturn(listOf(arma3ServerWithId(1L)))

        migrationTask.migrateToPerInstanceDirectories()

        val dest = arma3InstancePaths.getInstanceProfilesPath(1L).resolve("Users").resolve("ARMA3_1")
        assertThat(dest.resolve("save.bin")).exists()
    }

    @Test
    fun `does not copy non-pbo files from shared mpmissions`() {
        val mpmissions = arma3Root.resolve("mpmissions")
        Files.createDirectories(mpmissions)
        Files.createFile(mpmissions.resolve("readme.txt"))
        Files.createFile(mpmissions.resolve("mission.pbo"))
        `when`(serverRepository.findAll()).thenReturn(listOf(arma3ServerWithId(1L)))

        migrationTask.migrateToPerInstanceDirectories()

        val destDir = arma3InstancePaths.getInstanceMpmissionsPath(1L)
        assertThat(destDir.resolve("mission.pbo")).exists()
        assertThat(destDir.resolve("readme.txt")).doesNotExist()
    }
}
