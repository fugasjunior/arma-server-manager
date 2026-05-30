package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;

public class SteamAuthNotSetException extends CustomUserErrorException {

    public SteamAuthNotSetException() {
        super("Steam Auth is not set");
    }
}

