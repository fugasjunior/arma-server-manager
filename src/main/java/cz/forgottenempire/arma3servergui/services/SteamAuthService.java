package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.dtos.SteamAuthDto;
import cz.forgottenempire.arma3servergui.model.SteamAuth;

public interface SteamAuthService {
    SteamAuth getAuthAccount();
    void setAuthAccount(SteamAuthDto auth);
}
