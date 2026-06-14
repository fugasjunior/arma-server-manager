package cz.forgottenempire.servermanager.common

import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files
import java.nio.file.Path

data class ProvidedKeyInfo(val name: String, val source: String) {
    companion object {
        const val BASE_GAME_SOURCE = "Base game"
    }
}

@Component
class Arma3KeyService @Autowired constructor(
    private val pathsFactory: PathsFactory,
    private val arma3InstancePaths: Arma3InstancePaths
) {

    fun rebuildInstanceBikeys(server: Arma3Server) {
        val id = server.id
        val instanceKeys = arma3InstancePaths.getInstanceKeysPath(id)

        clearExistingBikeys(instanceKeys)
        copyModBikeys(server, instanceKeys)
        copyCustomBikeys(server.id, instanceKeys)

        log.debug("Rebuilt instance keys for server {} in {}", id, instanceKeys)
    }

    fun listProvidedKeys(server: Arma3Server): List<ProvidedKeyInfo> {
        val result = mutableListOf<ProvidedKeyInfo>()
        for (entry in server.activeMods) {
            val source = entry.mod.name ?: entry.mod.id.toString()
            findBikeys(pathsFactory.getModInstallationPath(entry.mod.id, ServerType.ARMA3))
                .mapTo(result) { ProvidedKeyInfo(it.name, source) }
        }
        for (entry in server.activeLocalMods) {
            val source = entry.mod.name
            findBikeys(pathsFactory.getLocalModPath(source, ServerType.ARMA3))
                .mapTo(result) { ProvidedKeyInfo(it.name, source) }
        }
        findBikeys(pathsFactory.getServerKeysPath(ServerType.ARMA3))
            .mapTo(result) { ProvidedKeyInfo(it.name, ProvidedKeyInfo.BASE_GAME_SOURCE) }
        result.sortWith(compareBy({ it.source }, { it.name }))
        return result
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

    private fun copyCustomBikeys(serverId: Long, instanceKeys: Path) {
        copyBikeys(arma3InstancePaths.getInstanceCustomBikeysPath(serverId), instanceKeys)
    }

    private fun copyBikeys(src: Path, dst: Path) {
        findBikeys(src).forEach { key ->
            log.debug("Copying bikey {} to {}", key, dst)
            FileUtils.copyFileToDirectory(key, dst.toFile())
        }
    }

    private fun findBikeys(src: Path): List<File> {
        val srcDir = src.toFile()
        if (!srcDir.isDirectory) return emptyList()
        return FileUtils.iterateFiles(srcDir, arrayOf("bikey"), true).asSequence().toList()
    }

    companion object {
        private val log = LoggerFactory.getLogger(Arma3KeyService::class.java)
    }
}
