package cz.forgottenempire.arma3servergui.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "server has not been initalized yet")
public class ServerNotInitializedException extends RuntimeException{

    public ServerNotInitializedException() {
    }

    public ServerNotInitializedException(String message) {
        super(message);
    }
}
