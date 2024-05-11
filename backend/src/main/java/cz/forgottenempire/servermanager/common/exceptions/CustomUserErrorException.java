package cz.forgottenempire.servermanager.common.exceptions;

import org.springframework.http.HttpStatus;

public class CustomUserErrorException extends RuntimeException {

    private final HttpStatus httpStatus;

    public CustomUserErrorException() {
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    public CustomUserErrorException(String message) {
        super(message);
        httpStatus = HttpStatus.BAD_REQUEST;
    }

    public CustomUserErrorException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
