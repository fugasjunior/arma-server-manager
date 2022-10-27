package cz.forgottenempire.arma3servergui.server.serverinstance.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "port already taken")
public class PortAlreadyTakenException extends RuntimeException{

    public PortAlreadyTakenException() {
    }

    public PortAlreadyTakenException(String message) {
        super(message);
    }
}

