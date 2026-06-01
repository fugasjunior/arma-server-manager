package cz.forgottenempire.servermanager.serverinstance

import cz.forgottenempire.servermanager.common.Arma3InstancePaths
import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.ServerType
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

/**
 * One-time startup task that migrates existing Arma 3 per-server data from the old shared
 * directory layout into the new per-instance directories.
 *
 * Migrated assets:
 * - **Profile directories** (savegames, persistence): moved from
 *   `servers/ARMA3/custom_profiles/<sub>/ARMA3_<id>/` to
 *   `servers/ARMA3/profiles/ARMA3_<id>/<sub>/ARMA3_<id>/`.
 * - **Scenarios** (*.pbo): copied from the old shared `servers/ARMA3/mpmissions/` into
 *   each instance's `servers/ARMA3/profiles/ARMA3_<id>/mpmissions/`.
 *
 * Configs and keys need no migration — configs are regenerated from the database at
 * every startup, and keys are rebuilt from active mods before each server launch.
 *
 * A marker file (`servers/ARMA3/.instance-dirs-migrated`) guards the task so it runs
 * exactly once. Per-server failures are logged and do not abort startup.
 */
@Component
class Arma3InstanceDataMigrationTask @Autowired constructor(
    private val serverRepository: ServerRepository,
    private val pathsFactory: PathsFactory,
    private val arma3InstancePaths: Arma3InstancePaths
) {

    @EventListener(ApplicationReadyEvent::class)
    fun migrateToPerInstanceDirectories() {
        val arma3Root = pathsFactory.getServerPath(ServerType.ARMA3)
        if (!arma3Root.toFile().isDirectory) return

        val markerFile = arma3Root.resolve(MARKER_FILE)
        if (markerFile.toFile().exists()) return

        log.info("Migrating Arma 3 data to per-instance directories...")

        val servers = serverRepository.findAll().filterIsInstance<Arma3Server>()
        var migratedCount = 0
        for (server in servers) {
            try {
                migrateProfileDir(arma3Root, server.id)
                copySharedScenarios(arma3Root, server.id)
                migratedCount++
            } catch (e: Exception) {
                log.error("Failed to migrate data for Arma 3 server id={}", server.id, e)
            }
        }

        Files.createFile(markerFile)
        log.info("Arma 3 per-instance directory migration complete ({} servers processed)", migratedCount)
    }

    /**
     * Moves the server's profile directory (savegames, persistence data) from the old
     * shared `custom_profiles/<sub>/ARMA3_<id>` location to the new per-instance path.
     * Both `home` (Linux) and `Users` (Windows) subdirs are checked — a config may have
     * been created on a different OS than the one currently running.
     */
    private fun migrateProfileDir(arma3Root: Path, id: Long) {
        val dirName = Arma3InstancePaths.instanceDirName(id)
        val subdirs = listOf(OS_SUBDIR_LINUX, OS_SUBDIR_WINDOWS)
        for (sub in subdirs) {
            val src = arma3Root.resolve("custom_profiles").resolve(sub).resolve(dirName)
            if (!src.toFile().isDirectory) continue

            val dest = arma3InstancePaths.getInstanceProfilesPath(id).resolve(sub).resolve(dirName)
            if (dest.toFile().exists()) {
                log.debug("Profile dir already exists at {}, skipping move", dest)
                continue
            }

            Files.createDirectories(dest.parent)
            FileUtils.moveDirectory(src.toFile(), dest.toFile())
            log.debug("Moved profile dir from {} to {}", src, dest)
        }
    }

    /**
     * Copies *.pbo files from the old shared `mpmissions/` dir into the instance's
     * own mpmissions directory. Existing files are skipped to avoid overwriting anything
     * that was already placed there by an earlier (partial) migration.
     */
    private fun copySharedScenarios(arma3Root: Path, id: Long) {
        val src = arma3Root.resolve("mpmissions")
        if (!src.toFile().isDirectory) return

        val destDir = arma3InstancePaths.getInstanceMpmissionsPath(id)
        Files.createDirectories(destDir)

        FileUtils.iterateFiles(src.toFile(), arrayOf("pbo"), false).forEach { pbo ->
            val dest = destDir.resolve(pbo.name)
            if (!dest.toFile().exists()) {
                FileUtils.copyFileToDirectory(pbo, destDir.toFile())
                log.debug("Copied scenario {} to {}", pbo.name, destDir)
            }
        }
    }

    companion object {
        private const val MARKER_FILE = ".instance-dirs-migrated"
        private const val OS_SUBDIR_LINUX = "home"
        private const val OS_SUBDIR_WINDOWS = "Users"
        private val log = LoggerFactory.getLogger(Arma3InstanceDataMigrationTask::class.java)
    }
}
