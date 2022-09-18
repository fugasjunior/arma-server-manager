package cz.forgottenempire.arma3servergui.workshop.services;

import cz.forgottenempire.arma3servergui.workshop.dtos.SteamAuthDto;
import cz.forgottenempire.arma3servergui.workshop.entities.SteamAuth;

public interface SteamAuthService {
    SteamAuth getAuthAccount();
    void setAuthAccount(SteamAuthDto auth);
}
