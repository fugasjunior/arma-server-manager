package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.AuthStatus
import cz.forgottenempire.servermanager.api.model.AuthType
import cz.forgottenempire.servermanager.api.model.AuthVerificationResultDto
import cz.forgottenempire.servermanager.api.model.SteamAuthDto
import cz.forgottenempire.servermanager.api.model.SteamAuthStatusDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

@ExtendWith(MockitoExtension::class)
class SteamAuthControllerUnitTest {

    @Mock
    private lateinit var steamAuthService: SteamAuthService

    @Mock(stubOnly = true)
    private lateinit var steamAuthVerifier: SteamAuthVerifier

    private lateinit var controller: SteamAuthController

    @BeforeEach
    fun setUp() {
        controller = SteamAuthController(steamAuthService, steamAuthVerifier)
    }

    @Test
    fun `when get auth account, then respond with auth account without password`() {
        val steamAuth = SteamAuth(1L, "username", "password", "DEFGH")
        `when`(steamAuthService.getAuthAccount()).thenReturn(steamAuth)

        val response: ResponseEntity<SteamAuthDto> = controller.getSteamAuth()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.username).isEqualTo("username")
        assertThat(response.body!!.steamGuardToken).isEqualTo("DEFGH")
        assertThat(response.body!!.password).isNull()
    }

    @Test
    fun `when set auth account, then steam auth service called`() {
        val dto = SteamAuthDto().apply {
            username = "username"
            password = "password"
            steamGuardToken = "DEFGH"
        }

        val response = controller.setSteamAuth(dto)

        verify(steamAuthService).setAuthAccount(dto)
        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNull()
    }

    @Test
    fun `when clear auth account, then auth service clear method called and no content response returned`() {
        val response = controller.clearSteamAuth()

        verify(steamAuthService).clearAuthAccount()
        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        assertThat(response.body).isNull()
    }

    @Test
    fun `when get auth status and auth is configured, then return true status`() {
        `when`(steamAuthService.isAuthConfigured()).thenReturn(true)

        val response: ResponseEntity<SteamAuthStatusDto> = controller.getSteamAuthStatus()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.isConfigured).isTrue()
    }

    @Test
    fun `when get auth status and auth is not configured, then return false status`() {
        `when`(steamAuthService.isAuthConfigured()).thenReturn(false)

        val response: ResponseEntity<SteamAuthStatusDto> = controller.getSteamAuthStatus()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.isConfigured).isFalse()
    }

    @Test
    fun `when verify credentials and verification succeeds, then return verification result`() {
        val authDto = SteamAuthDto().apply {
            username = "username"
            password = "password"
        }
        val verificationResult = AuthVerificationResult(
            status = AuthVerificationResult.AuthStatus.SUCCESS,
            authType = AuthVerificationResult.AuthType.NONE,
            message = "Authentication successful"
        )
        `when`(steamAuthVerifier.verifyCredentials(authDto)).thenReturn(verificationResult)

        val response: ResponseEntity<AuthVerificationResultDto> = controller.verifySteamAuth(authDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.status).isEqualTo(AuthStatus.SUCCESS)
        assertThat(response.body!!.authType).isEqualTo(AuthType.NONE)
        assertThat(response.body!!.message).isEqualTo("Authentication successful")
    }

    @Test
    fun `when verify credentials and verification throws exception, then return error result`() {
        val authDto = SteamAuthDto().apply {
            username = "username"
            password = "password"
        }
        `when`(steamAuthVerifier.verifyCredentials(authDto)).thenThrow(RuntimeException("Test exception"))

        val response: ResponseEntity<AuthVerificationResultDto> = controller.verifySteamAuth(authDto)

        assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.status).isEqualTo(AuthStatus.ERROR)
        assertThat(response.body!!.message).contains("Test exception")
        assertThat(response.body!!.authType).isEqualTo(AuthType.UNKNOWN)
    }
}
