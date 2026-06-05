package cz.forgottenempire.servermanager.keymgmt

import cz.forgottenempire.servermanager.api.model.Arma3KeyDto
import cz.forgottenempire.servermanager.common.Arma3InstancePaths
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.util.UriUtils
import java.io.IOException
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalDateTime
import java.time.ZoneId

@Service
class BiKeyService @Autowired constructor(
    private val arma3InstancePaths: Arma3InstancePaths,
    private val biKeyValidator: BiKeyValidator
) {

    fun listKeys(serverId: Long): List<Arma3KeyDto> {
        val dir = arma3InstancePaths.getInstanceCustomBikeysPath(serverId)
        val dirFile = dir.toFile()
        if (!dirFile.isDirectory) {
            return emptyList()
        }
        return FileUtils.iterateFiles(dirFile, arrayOf("bikey"), false)
            .asSequence()
            .map { file ->
                val dto = Arma3KeyDto()
                    .name(file.name)
                    .fileSize(file.length())
                setFileCreationTime(file.toPath(), dto)
                dto
            }
            .toList()
    }

    fun uploadKey(serverId: Long, file: MultipartFile) {
        biKeyValidator.validate(file)

        val dir = arma3InstancePaths.getInstanceCustomBikeysPath(serverId)
        Files.createDirectories(dir)

        val fileName = UriUtils.decode(file.originalFilename!!, Charset.defaultCharset())
        log.info("Handling bikey upload {} (size {}) for server {}", fileName, file.size, serverId)
        try {
            file.transferTo(dir.resolve(fileName).toFile())
            log.info("Successfully uploaded bikey {} for server {}", fileName, serverId)
        } catch (e: IOException) {
            log.error("Could not upload bikey {} for server {}", fileName, serverId, e)
            throw RuntimeException(e)
        }
    }

    fun deleteKey(serverId: Long, name: String): Boolean {
        val path = arma3InstancePaths.getInstanceCustomBikeysPath(serverId)
            .resolve(UriUtils.decode(name, Charset.defaultCharset()))
        return try {
            Files.delete(path)
            log.info("Deleted bikey {} for server {}", name, serverId)
            true
        } catch (e: IOException) {
            log.error("Could not delete bikey {} for server {}", name, serverId, e)
            false
        }
    }

    private fun setFileCreationTime(path: Path, dto: Arma3KeyDto) {
        try {
            val attr = Files.readAttributes(path, BasicFileAttributes::class.java)
            val dateTime = LocalDateTime.ofInstant(attr.creationTime().toInstant(), ZoneId.systemDefault())
            dto.createdOn(dateTime.atZone(ZoneId.systemDefault()).toOffsetDateTime())
        } catch (_: IOException) {
            log.warn("Could not get creation time for bikey file '{}'", path.fileName)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BiKeyService::class.java)
    }
}
