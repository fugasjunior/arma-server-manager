package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.AuthType
import cz.forgottenempire.servermanager.api.model.SteamLoginResult
import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.ProcessFactory
import cz.forgottenempire.servermanager.steamcmd.SteamCmdParameters
import org.apache.commons.io.FileUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.stream.Collectors

data class SteamLoginServiceResult(
    val result: SteamLoginResult,
    val authType: AuthType? = null,
    val message: String? = null
)

@Service
class SteamLoginService(
    private val processFactory: ProcessFactory,
    private val pathsFactory: PathsFactory,
    private val authService: SteamAuthService,
    private val sessionStatusHolder: SteamSessionStatusHolder
) {
    private val loginInProgress = AtomicBoolean(false)

    fun isLoginInProgress(): Boolean = loginInProgress.get()

    fun login(username: String, password: String, steamGuardCode: String?): SteamLoginServiceResult {
        if (!loginInProgress.compareAndSet(false, true)) {
            return SteamLoginServiceResult(SteamLoginResult.ERROR, message = "Another login is already in progress.")
        }
        return try {
            performLogin(username, password, steamGuardCode)
        } finally {
            loginInProgress.set(false)
        }
    }

    private fun performLogin(username: String, password: String, steamGuardCode: String?): SteamLoginServiceResult {
        FileUtils.deleteQuietly(pathsFactory.getSteamCmdCacheFile())

        val params = SteamCmdParameters.Builder()
            .withCredentialsLogin(username, password, steamGuardCode)
            .build()

        val steamCmdFile = pathsFactory.getSteamCmdExecutable()
        val process = processFactory.startProcessWithUnbufferedOutput(steamCmdFile, params.get())

        val output = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
            reader.lines()
                .map { it.replace(Regex("\\[[;\\d]*m"), "") }
                .map { it.replace(Regex("\\p{C}"), "") }
                .collect(Collectors.joining("\n"))
        }

        if (!process.waitFor(LOGIN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly()
            log.error("SteamCMD login timed out after {}s", LOGIN_TIMEOUT_SECONDS)
            return SteamLoginServiceResult(SteamLoginResult.ERROR, message = "Login timed out.")
        }

        return classifyOutput(output, username, password)
    }

    private fun classifyOutput(output: String, username: String, password: String): SteamLoginServiceResult {
        val lower = output.lowercase()

        if (lower.contains("to steam public...ok") || lower.contains("waiting for confirmation...ok")) {
            authService.saveCredentials(username, password)
            sessionStatusHolder.setActive()
            return SteamLoginServiceResult(SteamLoginResult.SUCCESS, AuthType.NONE, "Login successful.")
        }

        if (lower.contains("steam guard code was invalid")) {
            return SteamLoginServiceResult(SteamLoginResult.INVALID_CODE, AuthType.EMAIL, "Invalid Steam Guard code.")
        }

        if (lower.contains("two-factor code mismatch")) {
            return SteamLoginServiceResult(SteamLoginResult.INVALID_CODE, AuthType.MOBILE, "Incorrect authenticator code.")
        }

        if (lower.contains("invalid password")) {
            return SteamLoginServiceResult(SteamLoginResult.INVALID_CREDENTIALS, AuthType.NONE, "Invalid username or password.")
        }

        if (lower.contains("rate limit exceeded")) {
            return SteamLoginServiceResult(SteamLoginResult.RATE_LIMITED, message = "Too many login attempts. Please try again later.")
        }

        if (lower.contains("check your email for the message")) {
            return SteamLoginServiceResult(SteamLoginResult.CODE_REQUIRED, AuthType.EMAIL, "Steam Guard code required. Please check your email.")
        }

        // Push confirmation timed out → user needs to enter TOTP code manually
        if (lower.contains("timed out waiting for confirmation") || lower.contains("wait for confirmation timed out")) {
            return SteamLoginServiceResult(
                SteamLoginResult.CODE_REQUIRED, AuthType.MOBILE,
                "Mobile login timed out. Please enter your authenticator code."
            )
        }

        if (lower.contains("mobile authenticator")) {
            return SteamLoginServiceResult(
                SteamLoginResult.CODE_REQUIRED, AuthType.MOBILE,
                "Please confirm the login in your Steam mobile app or enter your authenticator code."
            )
        }

        log.error("Unrecognized SteamCMD login output:\n{}", output)
        return SteamLoginServiceResult(SteamLoginResult.ERROR, message = "An unexpected error occurred during login.")
    }

    companion object {
        private const val LOGIN_TIMEOUT_SECONDS = 120L
        private val log = LoggerFactory.getLogger(SteamLoginService::class.java)
    }
}
