package cz.forgottenempire.servermanager.workshop

import cz.forgottenempire.servermanager.api.model.AppSettingsDto
import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Service
class AppSettingsService(
    private val repository: AppSettingsRepository,
    private val scheduler: WorkshopCronScheduler
) {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    fun getSettings(): AppSettings {
        return repository.findById(1L).orElseThrow()
    }

    fun updateSettings(dto: AppSettingsDto): AppSettings {
        val enabled = dto.automaticModUpdateEnabled
            ?: throw CustomUserErrorException("automaticModUpdateEnabled must not be null", HttpStatus.BAD_REQUEST)

        val timeStr = dto.automaticModUpdateTime
            ?: throw CustomUserErrorException("automaticModUpdateTime is required", HttpStatus.BAD_REQUEST)

        val time = try {
            LocalTime.parse(timeStr, timeFormatter)
        } catch (_: Exception) {
            throw CustomUserErrorException("automaticModUpdateTime must be in HH:mm format", HttpStatus.BAD_REQUEST)
        }

        val settings = repository.findById(1L).orElseThrow()
        settings.automaticModUpdateEnabled = enabled
        settings.automaticModUpdateTime = time
        repository.save(settings)
        scheduler.reschedule(settings)
        return settings
    }

    fun toDto(settings: AppSettings): AppSettingsDto {
        return AppSettingsDto()
            .automaticModUpdateEnabled(settings.automaticModUpdateEnabled)
            .automaticModUpdateTime(settings.automaticModUpdateTime.format(timeFormatter))
    }
}
