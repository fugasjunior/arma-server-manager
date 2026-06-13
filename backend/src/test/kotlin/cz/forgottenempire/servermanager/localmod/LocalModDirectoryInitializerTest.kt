package cz.forgottenempire.servermanager.localmod

import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.ServerType
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path

class LocalModDirectoryInitializerTest {

    @TempDir
    private lateinit var tempDir: Path

    private lateinit var pathsFactory: PathsFactory
    private lateinit var initializer: LocalModDirectoryInitializer

    @BeforeEach
    fun setUp() {
        pathsFactory = PathsFactory(
            tempDir.toString(),
            tempDir.resolve("mods").toString(),
            tempDir.resolve("logs").toString(),
            "/steamcmd/steamcmd",
            "/steamcmd/cache.json"
        )
        initializer = LocalModDirectoryInitializer(pathsFactory)
    }

    @Test
    fun `creates ARMA3 and DAYZ local mod directories when absent`() {
        initializer.createLocalModDirectories()

        assertThat(pathsFactory.getLocalModsBasePath(ServerType.ARMA3)).isDirectory()
        assertThat(pathsFactory.getLocalModsBasePath(ServerType.DAYZ)).isDirectory()
    }

    @Test
    fun `creates README txt in each directory`() {
        initializer.createLocalModDirectories()

        assertThat(pathsFactory.getLocalModsBasePath(ServerType.ARMA3).resolve("README.txt")).isRegularFile()
        assertThat(pathsFactory.getLocalModsBasePath(ServerType.DAYZ).resolve("README.txt")).isRegularFile()
    }

    @Test
    fun `does not overwrite existing README txt`() {
        val arma3Dir = pathsFactory.getLocalModsBasePath(ServerType.ARMA3)
        Files.createDirectories(arma3Dir)
        val readme = arma3Dir.resolve("README.txt")
        Files.writeString(readme, "custom content")

        initializer.createLocalModDirectories()

        assertThat(readme).hasContent("custom content")
    }

    @Test
    fun `is idempotent when directories already exist`() {
        Files.createDirectories(pathsFactory.getLocalModsBasePath(ServerType.ARMA3))
        Files.createDirectories(pathsFactory.getLocalModsBasePath(ServerType.DAYZ))

        assertThatCode { initializer.createLocalModDirectories() }.doesNotThrowAnyException()
    }

    @Test
    fun `continues and does not throw when a directory cannot be created`() {
        // Block ARMA3 path by placing a regular file at the intermediate "local" node
        val localNode = tempDir.resolve("mods").resolve("local")
        Files.createDirectories(localNode.parent)
        Files.createFile(localNode)

        assertThatCode { initializer.createLocalModDirectories() }.doesNotThrowAnyException()
    }
}
