package cz.forgottenempire.servermanager.serverinstance

import cz.forgottenempire.servermanager.serverinstance.entities.ServerConfigOverride
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ServerConfigOverrideRepository : JpaRepository<ServerConfigOverride, Long> {

    fun findByServerIdAndConfigKey(serverId: Long, configKey: ConfigFileKey): ServerConfigOverride?

    fun findByServerId(serverId: Long): List<ServerConfigOverride>

    fun deleteByServerIdAndConfigKey(serverId: Long, configKey: ConfigFileKey)
}
