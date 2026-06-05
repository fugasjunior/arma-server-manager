package cz.forgottenempire.servermanager.common

import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

@Component
class Arma3KeyService @Autowired constructor(
    private val pathsFactory: PathsFactory,
    private val arma3InstancePaths: Arma3InstancePaths
) {

    fun rebuildInstanceBikeys(server: Arma3Server) {
        val id = server.id
        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(id)

        clearExistingBikeys(instanceKeys)
        copyGameBikeys(instanceKeys)
        copyModBikeys(server, instanceKeys)
        copyCustomBikeys(server.id, instanceKeys)

        log.debug("Rebuilt instance keys for server {} in {}", id, instanceKeys)
    }

    private fun clearExistingBikeys(instanceKeys: Path) {
        FileUtils.deleteDirectory(instanceKeys.toFile())
        Files.createDirectories(instanceKeys)
    }

    private fun copyModBikeys(server: Arma3Server, instanceKeys: Path) {
        for (entry in server.activeMods) {
            copyBikeys(pathsFactory.getModInstallationPath(entry.mod.id, ServerType.ARMA3), instanceKeys)
        }
        for (entry in server.activeLocalMods) {
            copyBikeys(pathsFactory.getLocalModPath(entry.mod.name, ServerType.ARMA3), instanceKeys)
        }
    }

    private fun copyGameBikeys(instanceKeys: Path) {
        copyBikeys(pathsFactory.getServerKeysPath(ServerType.ARMA3), instanceKeys)
    }

    private fun copyCustomBikeys(serverId: Long, instanceKeys: Path) {
        copyBikeys(arma3InstancePaths.getInstanceCustomBikeysPath(serverId), instanceKeys)
    }

    private fun copyBikeys(src: Path, dst: Path) {
        val srcDir = src.toFile()
        if (!srcDir.isDirectory) return
        FileUtils.iterateFiles(srcDir, arrayOf("bikey"), true).forEach { key ->
            FileUtils.copyFileToDirectory(key, dst.toFile())
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(Arma3KeyService::class.java)
    }
}
