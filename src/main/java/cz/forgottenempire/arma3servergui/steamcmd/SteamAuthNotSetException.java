package cz.forgottenempire.arma3servergui.steamcmd;

import cz.forgottenempire.arma3servergui.common.exceptions.CustomUserErrorException;

class SteamAuthNotSetException extends CustomUserErrorException {

    public SteamAuthNotSetException() {
        super("Steam Auth is not set");
    }
}

