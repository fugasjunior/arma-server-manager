package cz.forgottenempire.servermanager.workshop

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.ScheduledFuture

@Component
class WorkshopCronScheduler(
    private val taskScheduler: TaskScheduler,
    private val settingsRepository: AppSettingsRepository,
    private val modsFacade: WorkshopModsFacade
) {
    private val log = LoggerFactory.getLogger(WorkshopCronScheduler::class.java)
    private var scheduledJob: ScheduledFuture<*>? = null

    @PostConstruct
    fun initFromDb() {
        settingsRepository.findById(1L).ifPresent { schedule(it) }
    }

    fun reschedule(settings: AppSettings) {
        cancel()
        schedule(settings)
    }

    private fun cancel() {
        scheduledJob?.let {
            log.info("Cancelling scheduled mod update job")
            it.cancel(false)
            scheduledJob = null
        }
    }

    private fun schedule(settings: AppSettings) {
        cancel()
        if (!settings.automaticModUpdateEnabled) {
            log.info("Automatic mod update is disabled")
            return
        }
        val time = settings.automaticModUpdateTime
        val nextFire = nextInstant(time)
        log.info("Scheduling daily mod update at {}", nextFire.atZone(ZoneId.systemDefault()).toLocalTime())
        scheduledJob = taskScheduler.scheduleAtFixedRate(
            { runUpdate() },
            nextFire,
            Duration.ofDays(1)
        )
    }

    private fun runUpdate() {
        log.info("Running scheduled mod update")
        modsFacade.updateAllMods()
    }

    private fun nextInstant(time: LocalTime): Instant {
        val now = LocalDateTime.now()
        var next = LocalDateTime.of(now.toLocalDate(), time)
        if (now.isAfter(next)) {
            next = next.plusDays(1)
        }
        return next.atZone(ZoneId.systemDefault()).toInstant()
    }
}
