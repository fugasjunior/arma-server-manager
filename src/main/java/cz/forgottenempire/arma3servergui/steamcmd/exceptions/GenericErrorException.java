package cz.forgottenempire.arma3servergui.steamcmd.exceptions;

public class GenericErrorException extends RuntimeException {

    public GenericErrorException() {
    }

    public GenericErrorException(String message) {
        super(message);
    }
}
