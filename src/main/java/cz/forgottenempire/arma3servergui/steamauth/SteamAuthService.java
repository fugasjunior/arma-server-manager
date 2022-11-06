package cz.forgottenempire.arma3servergui.steamauth;

import cz.forgottenempire.arma3servergui.workshop.SteamAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SteamAuthService {

    private final SteamAuthRepository authRepository;

    @Autowired
    public SteamAuthService(SteamAuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Cacheable("steamAuthAccount")
    public SteamAuth getAuthAccount() {
        return authRepository.findAll().stream().findFirst().orElse(new SteamAuth());
    }

    @CacheEvict(value = "steamAuthAccount", allEntries = true)
    public void setAuthAccount(SteamAuthDto auth) {
        log.info("Setting new Steam Auth account: username '{}', token '{}'",
                auth.getUsername(), auth.getSteamGuardToken());
        SteamAuth persistedAuth = getAuthAccount();
        populateAuth(auth, persistedAuth);
        authRepository.save(persistedAuth);
    }

    private void populateAuth(SteamAuthDto auth, SteamAuth persistedAuth) {
        persistedAuth.setUsername(auth.getUsername());
        persistedAuth.setSteamGuardToken(auth.getSteamGuardToken());
        if (StringUtils.isNotBlank(auth.getPassword())) {
            persistedAuth.setPassword(auth.getPassword());
        }
    }
}
