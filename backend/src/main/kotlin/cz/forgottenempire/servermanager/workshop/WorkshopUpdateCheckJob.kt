package cz.forgottenempire.servermanager.workshop

import cz.forgottenempire.servermanager.common.InstallationStatus
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class WorkshopUpdateCheckJob(
    private val modsService: WorkshopModsService,
    private val metadataService: ModMetadataService
) {
    private val log = LoggerFactory.getLogger(WorkshopUpdateCheckJob::class.java)

    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.HOURS)
    fun checkForUpdates() {
        val finishedMods = modsService.getAllMods()
            .filter { it.installationStatus == InstallationStatus.FINISHED }

        if (finishedMods.isEmpty()) {
            return
        }

        log.info("Checking for Workshop updates for {} mod(s)", finishedMods.size)
        val modIds = finishedMods.map { it.id }
        val metadataMap = metadataService.fetchModMetadata(modIds)

        finishedMods.forEach { mod ->
            val timeUpdated = metadataMap[mod.id]?.timeUpdated ?: return@forEach
            mod.latestWorkshopUpdatedAt = timeUpdated
            modsService.saveMod(mod)
        }

        log.info("Workshop update check complete")
    }
}
