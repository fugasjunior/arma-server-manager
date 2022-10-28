package cz.forgottenempire.arma3servergui.steamcmd.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "steam auth is not set")
public class SteamAuthNotSetException extends RuntimeException {

    public SteamAuthNotSetException() {
        super("Steam Auth is not set");
    }

    public SteamAuthNotSetException(String message) {
        super(message);
    }
}
