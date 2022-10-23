package cz.forgottenempire.arma3servergui.steamcmd.exceptions;

public class NoMatchException extends RuntimeException {

    public NoMatchException() {
    }

    public NoMatchException(String message) {
        super(message);
    }
}
