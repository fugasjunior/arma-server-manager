package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.SteamAuthDto
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType
import cz.forgottenempire.servermanager.steamcmd.SteamCmdAuthService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.io.IOException

@ExtendWith(MockitoExtension::class)
class SteamAuthVerifierUnitTest {

    companion object {
        private const val USERNAME = "username"
        private const val PASSWORD = "password"
        private const val STEAM_GUARD_TOKEN = "ABCDE"
        private val AUTH_DTO = SteamAuthDto().username(USERNAME).password(PASSWORD).steamGuardToken(STEAM_GUARD_TOKEN)
        private val EXPECTED_AUTH = SteamAuth(null, USERNAME, PASSWORD, STEAM_GUARD_TOKEN)
    }

    @Mock(stubOnly = true)
    private lateinit var steamCmdAuthService: SteamCmdAuthService

    private lateinit var verifier: SteamCmdSteamAuthVerifier

    @BeforeEach
    fun setUp() {
        verifier = SteamCmdSteamAuthVerifier(steamCmdAuthService)
    }

    @Test
    fun `when verify credentials and verification succeeds, then return success result`() {
        val expectedResult = AuthVerificationResult(
            status = AuthStatus.SUCCESS,
            authType = AuthType.NONE,
            message = "Authentication successful"
        )
        `when`(steamCmdAuthService.verifyCredentials(EXPECTED_AUTH)).thenReturn(expectedResult)

        val result = verifier.verifyCredentials(AUTH_DTO)

        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `when verify credentials and verification requires 2FA, then return requires 2FA result`() {
        val expectedResult = AuthVerificationResult(
            status = AuthStatus.REQUIRES_2FA,
            authType = AuthType.MOBILE,
            message = "Mobile authentication required"
        )
        `when`(steamCmdAuthService.verifyCredentials(EXPECTED_AUTH)).thenReturn(expectedResult)

        val result = verifier.verifyCredentials(AUTH_DTO)

        assertThat(result).isEqualTo(expectedResult)
    }

    @Test
    fun `when verify credentials and verification throws exception, then return error result`() {
        `when`(steamCmdAuthService.verifyCredentials(EXPECTED_AUTH))
            .thenThrow(RuntimeException("Test exception"))

        val result = verifier.verifyCredentials(AUTH_DTO)

        assertThat(result.status).isEqualTo(AuthStatus.ERROR)
        assertThat(result.authType).isEqualTo(AuthType.UNKNOWN)
        assertThat(result.message).contains("Test exception")
    }

    @Test
    fun `when verify credentials and verification throws IOException, then return error result`() {
        `when`(steamCmdAuthService.verifyCredentials(EXPECTED_AUTH))
            .thenThrow(IOException("IO exception"))

        val result = verifier.verifyCredentials(AUTH_DTO)

        assertThat(result.status).isEqualTo(AuthStatus.ERROR)
        assertThat(result.authType).isEqualTo(AuthType.UNKNOWN)
        assertThat(result.message).contains("IO exception")
    }
}
