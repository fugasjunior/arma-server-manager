package cz.forgottenempire.steamcmd.exceptions;

public class GenericErrorException extends RuntimeException {

    public GenericErrorException() {
    }

    public GenericErrorException(String message) {
        super(message);
    }
}
