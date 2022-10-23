package cz.forgottenempire.arma3servergui.server.serverinstance.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ModifyingRunningServerException extends RuntimeException{

    public ModifyingRunningServerException() {
    }

    public ModifyingRunningServerException(String message) {
        super(message);
    }
}

