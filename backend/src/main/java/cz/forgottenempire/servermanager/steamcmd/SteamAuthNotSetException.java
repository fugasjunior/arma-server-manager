package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;

class SteamAuthNotSetException extends CustomUserErrorException {

    public SteamAuthNotSetException() {
        super("Steam Auth is not set");
    }
}

