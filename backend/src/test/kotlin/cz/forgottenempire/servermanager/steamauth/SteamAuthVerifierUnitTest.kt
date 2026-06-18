package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.AuthType
import cz.forgottenempire.servermanager.api.model.SteamLoginResult
import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.ProcessFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyList
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.io.File

private const val USERNAME = "username"
private const val PASSWORD = "password"

@ExtendWith(MockitoExtension::class)
class SteamLoginServiceUnitTest {

    @Mock(stubOnly = true)
    private lateinit var processFactory: ProcessFactory

    @Mock(stubOnly = true)
    private lateinit var pathsFactory: PathsFactory

    @Mock
    private lateinit var authService: SteamAuthService

    @Mock
    private lateinit var sessionStatusHolder: SteamSessionStatusHolder

    private lateinit var loginService: SteamLoginService

    @BeforeEach
    fun setUp() {
        `when`(pathsFactory.getSteamCmdCacheFile()).thenReturn(File.createTempFile("config", ".vdf"))
        `when`(pathsFactory.getSteamCmdExecutable()).thenReturn(File("/usr/bin/steamcmd"))
        loginService = SteamLoginService(processFactory, pathsFactory, authService, sessionStatusHolder)
    }

    private fun fakeProcess(output: String, exitsInTime: Boolean = true): Process {
        val process = mock(Process::class.java)
        `when`(process.inputStream).thenReturn(output.byteInputStream())
        `when`(process.waitFor(anyLong(), any())).thenReturn(exitsInTime)
        return process
    }

    private fun givenSteamCmdOutputs(output: String, exitsInTime: Boolean = true) {
        val process = fakeProcess(output, exitsInTime)
        `when`(processFactory.startProcessWithUnbufferedOutput(any(), anyList()))
            .thenReturn(process)
    }

    @Test
    fun `when login succeeds without 2FA, then return SUCCESS`() {
        givenSteamCmdOutputs(
            "Logging in user '$USERNAME' [U:1:0] to Steam Public...OK\n" +
            "Waiting for user info...Waiting for compat in post-logon took: 0.05sOK"
        )

        val result = loginService.login(USERNAME, PASSWORD, null)

        assertThat(result.result).isEqualTo(SteamLoginResult.SUCCESS)
        assertThat(result.authType).isEqualTo(AuthType.NONE)
    }

    @Test
    fun `when email Steam Guard code is required, then return CODE_REQUIRED with EMAIL type`() {
        givenSteamCmdOutputs(
            "Logging in user '$USERNAME' [U:1:0] to Steam Public...\n" +
            "This computer has not been authenticated for your account using Steam Guard.\n" +
            "Please check your email for the message from Steam, and use\n" +
            "the command 'set_steam_guard_code' to enter the code here.\n" +
            "ERROR (Account Logon Denied)"
        )

        val result = loginService.login(USERNAME, PASSWORD, null)

        assertThat(result.result).isEqualTo(SteamLoginResult.CODE_REQUIRED)
        assertThat(result.authType).isEqualTo(AuthType.EMAIL)
    }

    @Test
    fun `when mobile push times out, then return CODE_REQUIRED with MOBILE type`() {
        givenSteamCmdOutputs(
            "Logging in user '$USERNAME' [U:1:0] to Steam Public...\n" +
            "This account is protected by a Steam Guard mobile authenticator.\n" +
            "Waiting for confirmation...\n" +
            "Wait for confirmation timed out.Timed out waiting for confirmation.\n" +
            "ERROR (Timeout)"
        )

        val result = loginService.login(USERNAME, PASSWORD, null)

        assertThat(result.result).isEqualTo(SteamLoginResult.CODE_REQUIRED)
        assertThat(result.authType).isEqualTo(AuthType.MOBILE)
    }

    @Test
    fun `when TOTP code is invalid, then return INVALID_CODE with MOBILE type`() {
        givenSteamCmdOutputs(
            "Logging in user '$USERNAME' [U:1:0] to Steam Public...\n" +
            "Two-factor code mismatch\n" +
            "ERROR (Invalid Login Auth Code)"
        )

        val result = loginService.login(USERNAME, PASSWORD, "WRONG")

        assertThat(result.result).isEqualTo(SteamLoginResult.INVALID_CODE)
        assertThat(result.authType).isEqualTo(AuthType.MOBILE)
    }

    @Test
    fun `when email Steam Guard code is invalid, then return INVALID_CODE with EMAIL type`() {
        givenSteamCmdOutputs(
            "Logging in user '$USERNAME' [U:1:0] to Steam Public...\n" +
            "Steam Guard code was invalid\n" +
            "ERROR (Invalid Login Auth Code)"
        )

        val result = loginService.login(USERNAME, PASSWORD, "BADCD")

        assertThat(result.result).isEqualTo(SteamLoginResult.INVALID_CODE)
        assertThat(result.authType).isEqualTo(AuthType.EMAIL)
    }

    @Test
    fun `when password is wrong, then return INVALID_CREDENTIALS`() {
        givenSteamCmdOutputs(
            "Logging in user '$USERNAME' [U:1:0] to Steam Public...\n" +
            "ERROR (Invalid Password)"
        )

        val result = loginService.login(USERNAME, "wrongpassword", null)

        assertThat(result.result).isEqualTo(SteamLoginResult.INVALID_CREDENTIALS)
    }

    @Test
    fun `when rate limited, then return RATE_LIMITED`() {
        givenSteamCmdOutputs(
            "Logging in user '$USERNAME' [U:1:0] to Steam Public...\n" +
            "ERROR (Rate Limit Exceeded)"
        )

        val result = loginService.login(USERNAME, PASSWORD, null)

        assertThat(result.result).isEqualTo(SteamLoginResult.RATE_LIMITED)
    }
}
