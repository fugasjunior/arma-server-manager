package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.api.model.SteamAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service for managing Steam authentication credentials and verification.
 */
@Service
@Slf4j
public class SteamAuthService {

    private final SteamAuthRepository authRepository;

    @Autowired
    SteamAuthService(SteamAuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Cacheable("steamAuthAccount")
    public SteamAuth getAuthAccount() {
        return authRepository.findAll().stream().findFirst().orElse(new SteamAuth());
    }

    @CacheEvict(value = "steamAuthAccount", allEntries = true)
    public void setAuthAccount(SteamAuthDto auth) {
        log.info("Setting new Steam Auth account: username '{}'",
                auth.getUsername());
        SteamAuth persistedAuth = getAuthAccount();
        populateAuth(auth, persistedAuth);
        authRepository.save(persistedAuth);
    }

    @CacheEvict(value = "steamAuthAccount", allEntries = true)
    public void clearAuthAccount() {
        log.info("Clearing Steam Auth");
        authRepository.deleteAll();
    }

    /**
     * Checks if Steam authentication is configured
     * @return true if Steam auth is configured, false otherwise
     */
    public boolean isAuthConfigured() {
        SteamAuth auth = getAuthAccount();
        return auth.getUsername() != null && auth.getPassword() != null;
    }

    private void populateAuth(SteamAuthDto auth, SteamAuth persistedAuth) {
        persistedAuth.setUsername(auth.getUsername());
        persistedAuth.setSteamGuardToken(auth.getSteamGuardToken());
        if (StringUtils.isNotBlank(auth.getPassword())) {
            persistedAuth.setPassword(auth.getPassword());
        }
    }
}