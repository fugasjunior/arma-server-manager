package cz.forgottenempire.servermanager.workshop

import cz.forgottenempire.servermanager.api.AppSettingsApi
import cz.forgottenempire.servermanager.api.model.AppSettingsDto
import cz.forgottenempire.servermanager.security.permission.PermissionCode
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

@RestController
@PreAuthorize("hasAuthority('" + PermissionCode.MANAGE_APP_SETTINGS + "')")
class AppSettingsController(
    private val service: AppSettingsService
) : AppSettingsApi {

    override fun getAppSettings(): ResponseEntity<AppSettingsDto> {
        return ResponseEntity.ok(service.toDto(service.getSettings()))
    }

    override fun updateAppSettings(appSettingsDto: AppSettingsDto): ResponseEntity<AppSettingsDto> {
        return ResponseEntity.ok(service.toDto(service.updateSettings(appSettingsDto)))
    }
}
