package cz.forgottenempire.arma3servergui.system.services;

import cz.forgottenempire.arma3servergui.workshop.dtos.SteamAuthDto;
import cz.forgottenempire.arma3servergui.system.entities.SteamAuth;

public interface SteamAuthService {
    SteamAuth getAuthAccount();
    void setAuthAccount(SteamAuthDto auth);
}
