package cz.forgottenempire.arma3servergui.services.impl;

import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.services.SteamCredentialsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DevCredentialsServiceImpl implements SteamCredentialsService {

    @Value("${steamAuth.username}")
    private String username;

    @Value("${steamAuth.password}")
    private String password;

    @Value("${steamAuth.token:}")
    private String token;

    @Override
    public SteamAuth getAuthAccount() {
        SteamAuth steamAuth = new SteamAuth();
        steamAuth.setUsername(username);
        steamAuth.setPassword(password);
        steamAuth.setSteamGuardToken(token);
        return steamAuth;
    }
}
