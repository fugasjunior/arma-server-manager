package cz.forgottenempire.arma3servergui.steamauth;

import cz.forgottenempire.arma3servergui.workshop.SteamAuthDto;

public interface SteamAuthService {

    SteamAuth getAuthAccount();

    void setAuthAccount(SteamAuthDto auth);
}
