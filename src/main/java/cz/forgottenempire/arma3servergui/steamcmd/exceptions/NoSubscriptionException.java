package cz.forgottenempire.arma3servergui.steamcmd.exceptions;

public class NoSubscriptionException extends RuntimeException {

    public NoSubscriptionException() {
    }

    public NoSubscriptionException(String message) {
        super(message);
    }
}
