package cz.forgottenempire.arma3servergui.serverinstance.exceptions;

public class ModifyingRunningServerException extends RuntimeException {

    public ModifyingRunningServerException() {
    }

    public ModifyingRunningServerException(String message) {
        super(message);
    }
}

