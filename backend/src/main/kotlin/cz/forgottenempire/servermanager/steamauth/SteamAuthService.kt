package cz.forgottenempire.servermanager.steamauth

import cz.forgottenempire.servermanager.api.model.SteamAuthDto
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class SteamAuthService(private val authRepository: SteamAuthRepository) {

    @Cacheable("steamAuthAccount")
    fun getAuthAccount(): SteamAuth =
        authRepository.findAll().firstOrNull() ?: SteamAuth()

    @CacheEvict(value = ["steamAuthAccount"], allEntries = true)
    fun setAuthAccount(auth: SteamAuthDto) {
        log.info("Setting new Steam Auth account: username '{}'", auth.username)
        val persistedAuth = getAuthAccount()
        populateAuth(auth, persistedAuth)
        authRepository.save(persistedAuth)
    }

    @CacheEvict(value = ["steamAuthAccount"], allEntries = true)
    fun clearAuthAccount() {
        log.info("Clearing Steam Auth")
        authRepository.deleteAll()
    }

    fun isAuthConfigured(): Boolean {
        val auth = getAuthAccount()
        return auth.username != null && auth.password != null
    }

    private fun populateAuth(auth: SteamAuthDto, persistedAuth: SteamAuth) {
        persistedAuth.username = auth.username
        persistedAuth.steamGuardToken = auth.steamGuardToken
        if (!auth.password.isNullOrBlank()) {
            persistedAuth.password = auth.password
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(SteamAuthService::class.java)
    }
}
