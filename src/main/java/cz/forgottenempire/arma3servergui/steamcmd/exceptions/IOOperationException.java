package cz.forgottenempire.arma3servergui.steamcmd.exceptions;

public class IOOperationException extends RuntimeException {

    public IOOperationException() {
    }

    public IOOperationException(String message) {
        super(message);
    }
}
