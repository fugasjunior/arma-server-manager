package cz.forgottenempire.servermanager.serverinstance

import cz.forgottenempire.servermanager.additionalserver.AdditionalServersService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class ServerAutoStartTask @Autowired constructor(
    private val serverInstanceService: ServerInstanceService,
    private val additionalServersService: AdditionalServersService
) {

    @EventListener(ApplicationReadyEvent::class)
    fun startAutoStartServers() {
        serverInstanceService.startAutoStartServers()
        additionalServersService.startAutoStartServers()
    }
}
