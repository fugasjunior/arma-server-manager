package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.AuthType
import cz.forgottenempire.servermanager.api.model.SessionStatus
import cz.forgottenempire.servermanager.api.model.SteamAuthDto
import cz.forgottenempire.servermanager.api.model.SteamAuthStatusDto
import cz.forgottenempire.servermanager.api.model.SteamLoginRequestDto
import cz.forgottenempire.servermanager.api.model.SteamLoginResult
import cz.forgottenempire.servermanager.api.model.SteamLoginResultDto
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

    @Mock
    private lateinit var loginService: SteamLoginService

    @Mock
    private lateinit var sessionStatusHolder: SteamSessionStatusHolder

    private lateinit var controller: SteamAuthController

    @BeforeEach
    fun setUp() {
        controller = SteamAuthController(steamAuthService, loginService, sessionStatusHolder)
    }

    @Test
    fun `when get auth account, then respond with username without password`() {
        val steamAuth = SteamAuth(1L, "username", "password")
        `when`(steamAuthService.getAuthAccount()).thenReturn(steamAuth)

        val response: ResponseEntity<SteamAuthDto> = controller.getSteamAuth()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotNull
        assertThat(response.body!!.username).isEqualTo("username")
        assertThat(response.body!!.password).isNull()
    }

    @Test
    fun `when clear auth account, then auth service clear method called and no content response returned`() {
        val response = controller.clearSteamAuth()

        verify(steamAuthService).clearAuthAccount()
        assertThat(response.statusCode).isEqualTo(HttpStatus.NO_CONTENT)
        assertThat(response.body).isNull()
    }

    @Test
    fun `when get auth status and auth is configured, then return configured with session status`() {
        `when`(steamAuthService.isAuthConfigured()).thenReturn(true)
        `when`(sessionStatusHolder.status).thenReturn(SessionStatus.ACTIVE)

        val response: ResponseEntity<SteamAuthStatusDto> = controller.getSteamAuthStatus()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.isConfigured).isTrue()
        assertThat(response.body!!.sessionStatus).isEqualTo(SessionStatus.ACTIVE)
    }

    @Test
    fun `when get auth status and auth is not configured, then return NOT_CONFIGURED session status`() {
        `when`(steamAuthService.isAuthConfigured()).thenReturn(false)

        val response: ResponseEntity<SteamAuthStatusDto> = controller.getSteamAuthStatus()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.isConfigured).isFalse()
        assertThat(response.body!!.sessionStatus).isEqualTo(SessionStatus.NOT_CONFIGURED)
    }

    @Test
    fun `when steam login succeeds, then return SUCCESS result`() {
        val request = SteamLoginRequestDto().apply { username = "username"; password = "password" }
        `when`(loginService.login("username", "password", null))
            .thenReturn(SteamLoginServiceResult(SteamLoginResult.SUCCESS, AuthType.NONE, "Login successful."))

        val response: ResponseEntity<SteamLoginResultDto> = controller.steamLogin(request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.result).isEqualTo(SteamLoginResult.SUCCESS)
    }

    @Test
    fun `when steam login needs email code, then return CODE_REQUIRED with EMAIL auth type`() {
        val request = SteamLoginRequestDto().apply { username = "username"; password = "password" }
        `when`(loginService.login("username", "password", null))
            .thenReturn(SteamLoginServiceResult(SteamLoginResult.CODE_REQUIRED, AuthType.EMAIL, "Check your email."))

        val response: ResponseEntity<SteamLoginResultDto> = controller.steamLogin(request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body!!.result).isEqualTo(SteamLoginResult.CODE_REQUIRED)
        assertThat(response.body!!.authType).isEqualTo(AuthType.EMAIL)
    }

    @Test
    fun `when steam login service throws exception, then return ERROR result`() {
        val request = SteamLoginRequestDto().apply { username = "username"; password = "password" }
        `when`(loginService.login("username", "password", null)).thenThrow(RuntimeException("Test exception"))

        val response: ResponseEntity<SteamLoginResultDto> = controller.steamLogin(request)

        assertThat(response.statusCode).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR)
        assertThat(response.body!!.result).isEqualTo(SteamLoginResult.ERROR)
        assertThat(response.body!!.message).contains("Test exception")
    }
}
