package cz.forgottenempire.servermanager.serverinstance

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import cz.forgottenempire.servermanager.api.model.ConfigOverrideDto
import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException
import cz.forgottenempire.servermanager.security.permission.PermissionCode
import cz.forgottenempire.servermanager.serverinstance.entities.Server
import cz.forgottenempire.servermanager.serverinstance.entities.ServerConfigOverride
import java.nio.file.Files
import java.nio.file.Path
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer

@Service
class ConfigOverrideService(
    private val pathsFactory: PathsFactory,
    private val freeMarkerConfigurer: FreeMarkerConfigurer,
    private val overrideRepository: ServerConfigOverrideRepository,
) {
    private val log = LoggerFactory.getLogger(ConfigOverrideService::class.java)

    fun seedConfigOverride(configKey: ConfigFileKey, serverId: Long?, transientEntity: Server): ConfigOverrideDto {
        if (!configKey.isValidFor(transientEntity.type.name)) {
            throw CustomUserErrorException(
                "ConfigKey $configKey is not valid for server type ${transientEntity.type}")
        }

        if (serverId != null) {
            val fileName = configKey.getConfigFileName(serverId)
            val configPath: Path = pathsFactory.getConfigFilePath(transientEntity.type, fileName)
            if (Files.exists(configPath)) {
                try {
                    val content = ServerConfig.readFromFile(configPath)
                    validateContent(configKey, content)
                    return ConfigOverrideDto()
                        .configKey(configKey.name)
                        .content(content)
                } catch (e: java.io.IOException) {
                    log.warn("Failed to read existing config file '{}'", configPath, e)
                }
            }
        }

        transientEntity.id = serverId ?: 0L
        val rendered = ServerConfig.renderToString(
            freeMarkerConfigurer,
            configKey.getTemplateName(),
            configKey.resolveTemplateModel(transientEntity))
        validateContent(configKey, rendered)
        return ConfigOverrideDto()
            .configKey(configKey.name)
            .content(rendered)
    }

    fun getServerOverrides(serverId: Long): List<ServerConfigOverride> =
        overrideRepository.findByServerId(serverId)

    fun loadOverridesMap(serverId: Long): Map<ConfigFileKey, String> {
        return overrideRepository.findByServerId(serverId)
            .filter { it.configKey != null && it.content != null }
            .associate { it.configKey!! to it.content!! }
    }

    fun syncOverrides(server: Server, overrides: List<ConfigOverrideDto>): Map<ConfigFileKey, String> {
        for (override in overrides) {
            val key = ConfigFileKey.valueOf(override.configKey)
            if (!key.isValidFor(server.type.name)) {
                throw IllegalArgumentException(
                    "ConfigKey $key is not valid for server type ${server.type}")
            }
            validateContent(key, override.content)
        }

        val existingOverrides = overrideRepository.findByServerId(server.id)
        val existingMap = existingOverrides
            .filter { it.configKey != null }
            .associate { it.configKey!! to (it.content ?: "") }
        val incomingMap = overrides
            .associate { ConfigFileKey.valueOf(it.configKey) to (it.content ?: "") }

        val changed = existingMap != incomingMap
        if (changed) {
            enforceConfigEditPermissions()
            val user = SecurityContextHolder.getContext().authentication!!.name
            log.info("Override change: user={} server={}({}) added={} removed={} modified={}",
                user, server.name, server.id,
                incomingMap.keys.filter { it !in existingMap.keys },
                existingMap.keys.filter { it !in incomingMap.keys },
                incomingMap.keys.filter { k -> k in existingMap.keys && existingMap[k] != incomingMap[k] })
            overrideRepository.deleteAll(existingOverrides)
            overrideRepository.flush()
            for (override in overrides) {
                val entity = ServerConfigOverride()
                entity.serverId = server.id
                entity.configKey = ConfigFileKey.valueOf(override.configKey)
                entity.content = override.content
                overrideRepository.save(entity)
            }
            overrideRepository.flush()
        }

        return incomingMap
    }

    private fun enforceConfigEditPermissions() {
        val auth = SecurityContextHolder.getContext().authentication!!
        val hasAdvanced = auth.authorities.any { a ->
            PermissionCode.ADVANCED_CONFIG_EDIT == a.authority }
        val hasSecrets = auth.authorities.any { a ->
            PermissionCode.SERVER_SECRETS_VIEW == a.authority }
        if (!hasAdvanced || !hasSecrets) {
            throw AccessDeniedException(
                "Changing config overrides requires both ADVANCED_CONFIG_EDIT and SERVER_SECRETS_VIEW permissions")
        }
    }

    private fun validateContent(key: ConfigFileKey, content: String?) {
        if (key != ConfigFileKey.REFORGER_JSON || content == null) return
        try {
            ObjectMapper().readTree(content)
        } catch (e: JsonProcessingException) {
            val loc = e.location
            throw CustomUserErrorException(
                "Invalid JSON in ${key.name}: ${e.originalMessage} at line ${loc.lineNr}, column ${loc.columnNr}")
        }
    }
}
