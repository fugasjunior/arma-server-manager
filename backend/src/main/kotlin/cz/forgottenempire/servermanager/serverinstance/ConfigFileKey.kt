package cz.forgottenempire.servermanager.serverinstance

import cz.forgottenempire.servermanager.serverinstance.entities.Arma3DifficultySettings
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3NetworkSettings
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server
import cz.forgottenempire.servermanager.serverinstance.entities.Server

enum class ConfigFileKey(private val validServerTypeNames: Set<String>) {
    ARMA3_SERVER_CFG(setOf("ARMA3")),
    ARMA3_PROFILE(setOf("ARMA3")),
    ARMA3_NETWORK_CFG(setOf("ARMA3")),
    DAYZ_SERVER_CFG(setOf("DAYZ", "DAYZ_EXP")),
    REFORGER_JSON(setOf("REFORGER"));

    fun isValidFor(serverTypeName: String): Boolean = serverTypeName in validServerTypeNames

    fun getConfigFileName(serverId: Long): String = when (this) {
        DAYZ_SERVER_CFG -> "DAYZ_${serverId}.cfg"
        ARMA3_SERVER_CFG -> "ARMA3_${serverId}.cfg"
        ARMA3_PROFILE -> "ARMA3_${serverId}.Arma3Profile"
        ARMA3_NETWORK_CFG -> "ARMA3_${serverId}_network.cfg"
        REFORGER_JSON -> "REFORGER_${serverId}.json"
    }

    fun getTemplateName(): String = when (this) {
        DAYZ_SERVER_CFG -> "serverConfigDayZ.ftl"
        ARMA3_SERVER_CFG -> "serverConfigArma3.ftl"
        ARMA3_PROFILE -> "arma3ServerProfile.ftl"
        ARMA3_NETWORK_CFG -> "arma3NetworkSettings.ftl"
        REFORGER_JSON -> "serverConfigReforger.ftl"
    }

    fun resolveTemplateModel(server: Server): Any = when (this) {
        DAYZ_SERVER_CFG, ARMA3_SERVER_CFG, REFORGER_JSON -> server
        ARMA3_PROFILE -> (server as Arma3Server).difficultySettings ?: Arma3DifficultySettings()
        ARMA3_NETWORK_CFG -> (server as Arma3Server).networkSettings ?: Arma3NetworkSettings()
    }
}
