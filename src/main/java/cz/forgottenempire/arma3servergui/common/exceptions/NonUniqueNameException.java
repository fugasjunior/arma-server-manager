package cz.forgottenempire.arma3servergui.common.exceptions;

import org.springframework.http.HttpStatus;

public class NonUniqueNameException extends CustomUserErrorException {

    public NonUniqueNameException() {
        this("The given name is already in use");
    }

    public NonUniqueNameException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
