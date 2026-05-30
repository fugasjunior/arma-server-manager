package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.api.model.SteamAuthDto;

public interface SteamAuthVerifier {
    AuthVerificationResult verifyCredentials(SteamAuthDto authDto);
}