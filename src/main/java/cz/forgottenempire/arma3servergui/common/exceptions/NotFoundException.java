package cz.forgottenempire.arma3servergui.common.exceptions;

import org.springframework.http.HttpStatus;

public class NotFoundException extends CustomUserErrorException {

    public NotFoundException() {
        this("Requested item does not exits");
    }

    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
