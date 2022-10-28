package cz.forgottenempire.arma3servergui.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "server is unsupported on your OS")
public class ServerUnsupportedOnOsException extends RuntimeException {

    public ServerUnsupportedOnOsException() {
    }

    public ServerUnsupportedOnOsException(String message) {
        super(message);
    }
}
