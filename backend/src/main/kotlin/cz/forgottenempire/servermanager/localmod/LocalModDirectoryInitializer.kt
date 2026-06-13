package cz.forgottenempire.servermanager.localmod

import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.ServerType
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.io.IOException
import java.nio.file.Files

@Component
class LocalModDirectoryInitializer(private val pathsFactory: PathsFactory) {

    private val log = LoggerFactory.getLogger(LocalModDirectoryInitializer::class.java)

    @EventListener(ApplicationReadyEvent::class)
    fun createLocalModDirectories() {
        listOf(ServerType.ARMA3, ServerType.DAYZ).forEach { serverType ->
            val path = pathsFactory.getLocalModsBasePath(serverType)
            try {
                Files.createDirectories(path)
                log.debug("Local mods directory ensured: {}", path)
                writeReadmeIfAbsent(path)
            } catch (e: IOException) {
                log.error("Failed to create local mods directory {}: {}", path, e.message)
            }
        }
    }

    private fun writeReadmeIfAbsent(dir: java.nio.file.Path) {
        val readme = dir.resolve("README.txt")
        if (Files.exists(readme)) return
        val content = """
            Local Mods Directory
            ====================

            Place each mod in its own subdirectory here. The directory name becomes the mod name.

            Example:
              ${dir.fileName}/
                my_mod/
                  addons/
                    my_mod.pbo
                  keys/
                    my_mod.bikey

            After adding or removing mod directories, click "Sync local mods" in the web UI.
            The sync will:
              - Register new mod directories and create the necessary symlinks in the server directory
              - Copy .bikey files to the server keys directory
              - Remove mods that no longer have a corresponding directory
        """.trimIndent()
        Files.writeString(readme, content)
    }
}
