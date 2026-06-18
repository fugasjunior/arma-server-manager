package cz.forgottenempire.servermanager.steamcmd

import cz.forgottenempire.servermanager.common.PathsFactory
import cz.forgottenempire.servermanager.common.ProcessFactory
import cz.forgottenempire.servermanager.steamauth.SteamAuthService
import cz.forgottenempire.servermanager.steamauth.SteamLoginService
import cz.forgottenempire.servermanager.steamauth.SteamSessionStatusHolder
import org.springframework.context.annotation.Lazy
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import java.util.stream.Collectors

@Service
class SteamSessionProbeService(
    private val steamAuthService: SteamAuthService,
    private val loginService: SteamLoginService,
    @Lazy private val steamCmdService: SteamCmdService,
    private val sessionStatusHolder: SteamSessionStatusHolder,
    private val processFactory: ProcessFactory,
    private val pathsFactory: PathsFactory
) {
    @Scheduled(fixedDelay = 15 * 60 * 1000)
    fun probe() {
        if (!steamAuthService.isAuthConfigured()) {
            sessionStatusHolder.setNotConfigured()
            return
        }
        if (steamCmdService.isBusy || loginService.isLoginInProgress()) {
            log.debug("Skipping session probe: SteamCMD busy or login in progress")
            return
        }
        val username = steamAuthService.getAuthAccount().username ?: return

        try {
            val params = SteamCmdParameters.Builder()
                .withCachedLogin(username)
                .build()
                .get()
            val process = processFactory.startProcessWithUnbufferedOutput(pathsFactory.getSteamCmdExecutable(), params)

            val output = BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                reader.lines()
                    .map { it.replace(Regex("\\[[;\\d]*m"), "") }
                    .map { it.replace(Regex("\\p{C}"), "") }
                    .collect(Collectors.joining("\n"))
            }

            if (!process.waitFor(PROBE_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                process.destroyForcibly()
                log.warn("Session probe timed out after {}s", PROBE_TIMEOUT_SECONDS)
                return
            }

            if (output.lowercase().contains("to steam public...ok")) {
                sessionStatusHolder.setActive()
                log.debug("Session probe: ACTIVE")
            } else {
                sessionStatusHolder.setExpired()
                log.debug("Session probe: EXPIRED — output: {}", output)
            }
        } catch (e: Exception) {
            log.error("Session probe failed", e)
        }
    }

    companion object {
        private const val PROBE_TIMEOUT_SECONDS = 60L
        private val log = LoggerFactory.getLogger(SteamSessionProbeService::class.java)
    }
}
