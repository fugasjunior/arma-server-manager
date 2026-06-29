package cz.forgottenempire.servermanager.serverinstance

import cz.forgottenempire.servermanager.common.Arma3InstancePaths
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.file.Files

/**
 * Copies per-instance filesystem assets from one Arma 3 server instance to another.
 *
 * Copied:
 * - `custom_bikeys/` — user-uploaded bikey files
 * - `mpmissions/` — uploaded scenario .pbo files
 * - profile subtree `<home|Users>/ARMA3_<id>/` — .Arma3Profile, savegames, and persistence data
 *
 * Not copied (regenerated at launch/save):
 * - `keys/` — rebuilt from active mods before each start
 * - `configs/` — generated from DB on save
 */
@Component
class Arma3InstanceDataCopier @Autowired constructor(
    private val arma3InstancePaths: Arma3InstancePaths
) {

    fun copyInstanceData(sourceId: Long, targetId: Long) {
        copyCustomBikeys(sourceId, targetId)
        copyMpmissions(sourceId, targetId)
        copyProfileSubtree(sourceId, targetId)
    }

    private fun copyCustomBikeys(sourceId: Long, targetId: Long) {
        val src = arma3InstancePaths.getInstanceCustomBikeysPath(sourceId)
        if (!src.toFile().isDirectory) return

        val dest = arma3InstancePaths.getInstanceCustomBikeysPath(targetId)
        Files.createDirectories(dest)
        FileUtils.copyDirectory(src.toFile(), dest.toFile())
        log.debug("Copied custom_bikeys from ARMA3_{} to ARMA3_{}", sourceId, targetId)
    }

    private fun copyMpmissions(sourceId: Long, targetId: Long) {
        val src = arma3InstancePaths.getInstanceMpmissionsPath(sourceId)
        if (!src.toFile().isDirectory) return

        val dest = arma3InstancePaths.getInstanceMpmissionsPath(targetId)
        Files.createDirectories(dest)
        FileUtils.iterateFiles(src.toFile(), arrayOf("pbo"), false).forEach { pbo ->
            FileUtils.copyFileToDirectory(pbo, dest.toFile())
        }
        log.debug("Copied mpmissions from ARMA3_{} to ARMA3_{}", sourceId, targetId)
    }

    private fun copyProfileSubtree(sourceId: Long, targetId: Long) {
        val sourceDirName = Arma3InstancePaths.instanceDirName(sourceId)
        val targetDirName = Arma3InstancePaths.instanceDirName(targetId)
        val osSubdirs = listOf("home", "Users")

        for (sub in osSubdirs) {
            val src = arma3InstancePaths.getInstanceProfilesPath(sourceId).resolve(sub).resolve(sourceDirName)
            if (!src.toFile().isDirectory) continue

            val dest = arma3InstancePaths.getInstanceProfilesPath(targetId).resolve(sub).resolve(targetDirName)
            Files.createDirectories(dest.parent)
            FileUtils.copyDirectory(src.toFile(), dest.toFile())
            log.debug("Copied profile dir ({}) from ARMA3_{} to ARMA3_{}", sub, sourceId, targetId)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Arma3InstanceDataCopier::class.java)
    }
}
