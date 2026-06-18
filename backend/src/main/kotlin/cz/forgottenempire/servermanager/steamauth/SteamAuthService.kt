package cz.forgottenempire.servermanager.steamauth

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
    fun saveCredentials(username: String, password: String) {
        log.info("Saving Steam auth credentials for username '{}'", username)
        val persistedAuth = getAuthAccount()
        persistedAuth.username = username
        persistedAuth.password = password
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

    companion object {
        private val log = LoggerFactory.getLogger(SteamAuthService::class.java)
    }
}
