package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.SteamAuthApi
import cz.forgottenempire.servermanager.api.model.AuthStatus
import cz.forgottenempire.servermanager.api.model.AuthType
import cz.forgottenempire.servermanager.api.model.AuthVerificationResultDto
import cz.forgottenempire.servermanager.api.model.SteamAuthDto
import cz.forgottenempire.servermanager.api.model.SteamAuthStatusDto
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RestController

private const val STEAM_AUTH_PERMISSION = "hasAuthority('STEAM_AUTH_ADMIN')"

@RestController
@PreAuthorize(STEAM_AUTH_PERMISSION)
class SteamAuthController(
    private val authService: SteamAuthService,
    private val authVerifier: SteamAuthVerifier
) : SteamAuthApi {

    override fun setSteamAuth(steamAuthDto: SteamAuthDto): ResponseEntity<Void> {
        authService.setAuthAccount(steamAuthDto)
        return ResponseEntity.ok().build()
    }

    override fun getSteamAuth(): ResponseEntity<SteamAuthDto> {
        val steamAuth = authService.getAuthAccount()
        val dto = SteamAuthDto()
            .username(steamAuth.username)
            .steamGuardToken(steamAuth.steamGuardToken)
        return ResponseEntity.ok(dto)
    }

    override fun clearSteamAuth(): ResponseEntity<Void> {
        authService.clearAuthAccount()
        return ResponseEntity.noContent().build()
    }

    override fun getSteamAuthStatus(): ResponseEntity<SteamAuthStatusDto> {
        val isConfigured = authService.isAuthConfigured()
        return ResponseEntity.ok(SteamAuthStatusDto().isConfigured(isConfigured))
    }

    override fun verifySteamAuth(steamAuthDto: SteamAuthDto): ResponseEntity<AuthVerificationResultDto> {
        return try {
            val result = authVerifier.verifyCredentials(steamAuthDto)
            val dto = AuthVerificationResultDto()
                .status(AuthStatus.fromValue(result.status!!.name))
                .message(result.message)
                .authType(AuthType.fromValue(result.authType!!.name))
            ResponseEntity.ok(dto)
        } catch (e: Exception) {
            val dto = AuthVerificationResultDto()
                .status(AuthStatus.ERROR)
                .message("Failed to verify credentials: ${e.message}")
                .authType(AuthType.UNKNOWN)
            ResponseEntity.internalServerError().body(dto)
        }
    }
}
