package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.SteamAuthDto
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType
import cz.forgottenempire.servermanager.steamcmd.SteamCmdAuthService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SteamCmdSteamAuthVerifier(
    private val steamCmdAuthService: SteamCmdAuthService
) : SteamAuthVerifier {

    override fun verifyCredentials(authDto: SteamAuthDto): AuthVerificationResult {
        val auth = SteamAuth(
            username = authDto.username,
            password = authDto.password,
            steamGuardToken = authDto.steamGuardToken
        )
        return try {
            steamCmdAuthService.verifyCredentials(auth)
        } catch (e: Exception) {
            log.error("Error during credential verification", e)
            AuthVerificationResult.builder()
                .status(AuthStatus.ERROR)
                .authType(AuthType.UNKNOWN)
                .message("Verification failed: ${e.message}")
                .build()
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SteamCmdSteamAuthVerifier::class.java)
    }
}
