package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.SteamAuthApi
import cz.forgottenempire.servermanager.api.model.SessionStatus
import cz.forgottenempire.servermanager.api.model.SteamAuthDto
import cz.forgottenempire.servermanager.api.model.SteamAuthStatusDto
import cz.forgottenempire.servermanager.api.model.SteamLoginRequestDto
import cz.forgottenempire.servermanager.api.model.SteamLoginResultDto
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

private const val STEAM_AUTH_PERMISSION = "hasAuthority('STEAM_AUTH_ADMIN')"

@RestController
@PreAuthorize(STEAM_AUTH_PERMISSION)
class SteamAuthController(
    private val authService: SteamAuthService,
    private val loginService: SteamLoginService,
    private val sessionStatusHolder: SteamSessionStatusHolder
) : SteamAuthApi {

    override fun steamLogin(steamLoginRequestDto: SteamLoginRequestDto): ResponseEntity<SteamLoginResultDto> {
        return try {
            val result = loginService.login(
                steamLoginRequestDto.username,
                steamLoginRequestDto.password,
                steamLoginRequestDto.steamGuardCode
            )
            val dto = SteamLoginResultDto()
                .result(result.result)
                .authType(result.authType)
                .message(result.message)
            ResponseEntity.ok(dto)
        } catch (e: Exception) {
            val dto = SteamLoginResultDto()
                .result(cz.forgottenempire.servermanager.api.model.SteamLoginResult.ERROR)
                .message("Login failed: ${e.message}")
            ResponseEntity.internalServerError().body(dto)
        }
    }

    override fun getSteamAuth(): ResponseEntity<SteamAuthDto> {
        val steamAuth = authService.getAuthAccount()
        val dto = SteamAuthDto().username(steamAuth.username)
        return ResponseEntity.ok(dto)
    }

    override fun clearSteamAuth(): ResponseEntity<Void> {
        authService.clearAuthAccount()
        return ResponseEntity.noContent().build()
    }

    override fun getSteamAuthStatus(): ResponseEntity<SteamAuthStatusDto> {
        val isConfigured = authService.isAuthConfigured()
        val sessionStatus = if (!isConfigured) SessionStatus.NOT_CONFIGURED else sessionStatusHolder.status
        val dto = SteamAuthStatusDto()
            .isConfigured(isConfigured)
            .sessionStatus(sessionStatus)
            .lastCheckedAt(sessionStatusHolder.lastCheckedAt)
        return ResponseEntity.ok(dto)
    }
}
