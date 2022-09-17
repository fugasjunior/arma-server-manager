package cz.forgottenempire.arma3servergui.workshop.services.impl;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.workshop.dtos.SteamAuthDto;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.workshop.repositories.SteamAuthRepository;
import cz.forgottenempire.arma3servergui.workshop.services.SteamAuthService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SteamAuthServiceImpl implements SteamAuthService {

    private SteamAuthRepository authRepository;

    @Override
    public SteamAuth getAuthAccount() {
        return authRepository.findById(Constants.ACCOUND_DEFAULT_ID).orElse(new SteamAuth());
    }

    @Override
    public void setAuthAccount(SteamAuthDto auth) {
        log.info("Setting new Steam Auth account: username {}, token {}",
                auth.getUsername(), auth.getSteamGuardToken());
        SteamAuth persistedAuth = authRepository.findById(Constants.ACCOUND_DEFAULT_ID)
                .orElse(createNewAuth(auth));
        populateAuth(auth, persistedAuth);
        authRepository.save(persistedAuth);
    }

    private SteamAuth createNewAuth(SteamAuthDto auth) {
        SteamAuth retVal = new SteamAuth(Constants.ACCOUND_DEFAULT_ID, "", "", "");
        if (auth != null) {
            retVal.setUsername(auth.getUsername());
            retVal.setPassword(auth.getPassword());
            retVal.setSteamGuardToken(auth.getSteamGuardToken());
        }
        return authRepository.save(retVal);
    }

    private void populateAuth(SteamAuthDto auth, SteamAuth persistedAuth) {
        persistedAuth.setUsername(auth.getUsername());
        persistedAuth.setSteamGuardToken(auth.getSteamGuardToken());
        if (StringUtils.isNotBlank(auth.getPassword())) {
            persistedAuth.setPassword(auth.getPassword());
        }
    }

    @Autowired
    public void setAuthRepository(SteamAuthRepository authRepository) {
        this.authRepository = authRepository;
    }
}
