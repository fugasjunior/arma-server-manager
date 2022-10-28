package cz.forgottenempire.arma3servergui.steamcmd;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "steam auth is not set")
class SteamAuthNotSetException extends RuntimeException {

    public SteamAuthNotSetException() {
        super("Steam Auth is not set");
    }

}

