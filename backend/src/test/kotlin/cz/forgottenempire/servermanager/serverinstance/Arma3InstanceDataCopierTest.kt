package cz.forgottenempire.servermanager.serverinstance

import cz.forgottenempire.servermanager.common.Arma3InstancePaths
import cz.forgottenempire.servermanager.common.PathsFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class Arma3InstanceDataCopierTest {

    @TempDir
    private lateinit var tempDir: Path

    private lateinit var arma3InstancePaths: Arma3InstancePaths
    private lateinit var copier: Arma3InstanceDataCopier

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
        copier = Arma3InstanceDataCopier(arma3InstancePaths)
    }

    @Test
    fun `copies custom_bikeys directory to target instance`() {
        val srcId = 1L
        val targetId = 2L
        val bikeysDir = arma3InstancePaths.getInstanceCustomBikeysPath(srcId)
        Files.createDirectories(bikeysDir)
        Files.createFile(bikeysDir.resolve("community.bikey"))

        copier.copyInstanceData(srcId, targetId)

        assertThat(arma3InstancePaths.getInstanceCustomBikeysPath(targetId).resolve("community.bikey")).exists()
    }

    @Test
    fun `copies pbo files from mpmissions to target instance`() {
        val srcId = 1L
        val targetId = 2L
        val mpmissionsDir = arma3InstancePaths.getInstanceMpmissionsPath(srcId)
        Files.createDirectories(mpmissionsDir)
        Files.createFile(mpmissionsDir.resolve("mission1.pbo"))
        Files.createFile(mpmissionsDir.resolve("mission2.pbo"))

        copier.copyInstanceData(srcId, targetId)

        val targetMpmissions = arma3InstancePaths.getInstanceMpmissionsPath(targetId)
        assertThat(targetMpmissions.resolve("mission1.pbo")).exists()
        assertThat(targetMpmissions.resolve("mission2.pbo")).exists()
    }

    @Test
    fun `does not copy non-pbo files from mpmissions`() {
        val srcId = 1L
        val targetId = 2L
        val mpmissionsDir = arma3InstancePaths.getInstanceMpmissionsPath(srcId)
        Files.createDirectories(mpmissionsDir)
        Files.createFile(mpmissionsDir.resolve("readme.txt"))
        Files.createFile(mpmissionsDir.resolve("mission.pbo"))

        copier.copyInstanceData(srcId, targetId)

        val targetMpmissions = arma3InstancePaths.getInstanceMpmissionsPath(targetId)
        assertThat(targetMpmissions.resolve("mission.pbo")).exists()
        assertThat(targetMpmissions.resolve("readme.txt")).doesNotExist()
    }

    @Test
    fun `copies home profile subtree with dir-name rewrite`() {
        val srcId = 1L
        val targetId = 2L
        val profileSrc = arma3InstancePaths.getInstanceProfilesPath(srcId)
            .resolve("home").resolve("ARMA3_$srcId")
        Files.createDirectories(profileSrc)
        Files.createFile(profileSrc.resolve("save.bin"))

        copier.copyInstanceData(srcId, targetId)

        val profileDest = arma3InstancePaths.getInstanceProfilesPath(targetId)
            .resolve("home").resolve("ARMA3_$targetId")
        assertThat(profileDest.resolve("save.bin")).exists()
    }

    @Test
    fun `copies Users profile subtree with dir-name rewrite`() {
        val srcId = 1L
        val targetId = 2L
        val profileSrc = arma3InstancePaths.getInstanceProfilesPath(srcId)
            .resolve("Users").resolve("ARMA3_$srcId")
        Files.createDirectories(profileSrc)
        Files.createFile(profileSrc.resolve("profile.Arma3Profile"))

        copier.copyInstanceData(srcId, targetId)

        val profileDest = arma3InstancePaths.getInstanceProfilesPath(targetId)
            .resolve("Users").resolve("ARMA3_$targetId")
        assertThat(profileDest.resolve("profile.Arma3Profile")).exists()
    }

    @Test
    fun `does not copy keys directory`() {
        val srcId = 1L
        val targetId = 2L
        val keysDir = arma3InstancePaths.getInstanceKeysPath(srcId)
        Files.createDirectories(keysDir)
        Files.createFile(keysDir.resolve("mod.bikey"))

        copier.copyInstanceData(srcId, targetId)

        assertThat(arma3InstancePaths.getInstanceKeysPath(targetId)).doesNotExist()
    }

    @Test
    fun `does not copy configs directory`() {
        val srcId = 1L
        val targetId = 2L
        val configsDir = arma3InstancePaths.getInstanceConfigsPath(srcId)
        Files.createDirectories(configsDir)
        Files.createFile(configsDir.resolve("server.cfg"))

        copier.copyInstanceData(srcId, targetId)

        assertThat(arma3InstancePaths.getInstanceConfigsPath(targetId)).doesNotExist()
    }

    @Test
    fun `is a no-op when source instance dirs are absent`() {
        copier.copyInstanceData(99L, 100L)

        assertThat(arma3InstancePaths.getInstanceBasePath(100L)).doesNotExist()
    }
}
