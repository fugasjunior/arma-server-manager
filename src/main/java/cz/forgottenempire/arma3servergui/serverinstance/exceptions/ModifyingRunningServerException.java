package cz.forgottenempire.arma3servergui.serverinstance.exceptions;

import cz.forgottenempire.arma3servergui.common.exceptions.CustomUserErrorException;

public class ModifyingRunningServerException extends CustomUserErrorException {

    public ModifyingRunningServerException() {
        super("Cannot modify running server");
    }

    public ModifyingRunningServerException(String message) {
        super(message);
    }
}

