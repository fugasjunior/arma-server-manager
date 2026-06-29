package cz.forgottenempire.servermanager.keymgmt

import cz.forgottenempire.servermanager.api.KeysApi
import cz.forgottenempire.servermanager.api.model.Arma3KeysDto
import cz.forgottenempire.servermanager.api.model.Arma3ProvidedKeyDto
import cz.forgottenempire.servermanager.api.model.Arma3ProvidedKeysDto
import cz.forgottenempire.servermanager.common.Arma3KeyService
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException
import cz.forgottenempire.servermanager.security.permission.PermissionCode
import cz.forgottenempire.servermanager.serverinstance.ServerInstanceService
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class BiKeyController @Autowired constructor(
    private val biKeyService: BiKeyService,
    private val arma3KeyService: Arma3KeyService,
    private val serverInstanceService: ServerInstanceService
) : KeysApi {

    @PreAuthorize("hasAuthority('${PermissionCode.BIKEY_VIEW}')")
    override fun getServerKeys(id: Long): ResponseEntity<Arma3KeysDto> {
        requireArma3Server(id)
        return ResponseEntity.ok(Arma3KeysDto().keys(biKeyService.listKeys(id)))
    }

    @PreAuthorize("hasAuthority('${PermissionCode.BIKEY_VIEW}')")
    override fun getServerProvidedKeys(id: Long): ResponseEntity<Arma3ProvidedKeysDto> {
        val server = getArma3Server(id)
        val keys = arma3KeyService.listProvidedKeys(server)
            .map { Arma3ProvidedKeyDto().name(it.name).source(it.source) }
        return ResponseEntity.ok(Arma3ProvidedKeysDto().keys(keys))
    }

    @PreAuthorize("hasAuthority('${PermissionCode.BIKEY_MODIFY}')")
    override fun uploadServerKeys(id: Long, file: List<MultipartFile>?): ResponseEntity<Arma3KeysDto> {
        requireArma3Server(id)
        file?.forEach { f ->
            log.info("Receiving bikey upload ({}) for server {}", f.originalFilename, id)
            if (f.originalFilename == null) {
                log.warn("Could not determine file name, skipping")
                return@forEach
            }
            biKeyService.uploadKey(id, f)
        }
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Arma3KeysDto().keys(biKeyService.listKeys(id)))
    }

    @PreAuthorize("hasAuthority('${PermissionCode.BIKEY_DELETE}')")
    override fun deleteServerKey(id: Long, name: String): ResponseEntity<Void> {
        requireArma3Server(id)
        log.info("Received request to delete bikey {} for server {}", name, id)
        if (!biKeyService.deleteKey(id, name)) {
            return ResponseEntity(HttpStatus.BAD_REQUEST)
        }
        return ResponseEntity.noContent().build()
    }

    private fun getArma3Server(id: Long): Arma3Server =
        serverInstanceService.getServer(id)
            .filter { it is Arma3Server }
            .map { it as Arma3Server }
            .orElseThrow { NotFoundException("Arma 3 server with ID $id doesn't exist") }

    private fun requireArma3Server(id: Long) {
        getArma3Server(id)
    }

    companion object {
        private val log = LoggerFactory.getLogger(BiKeyController::class.java)
    }
}
