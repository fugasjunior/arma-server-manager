package cz.forgottenempire.arma3servergui.steamcmd.exceptions;

public class LoginException extends RuntimeException {

    public LoginException() {
    }

    public LoginException(String message) {
        super(message);
    }
}
