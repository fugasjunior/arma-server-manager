package cz.forgottenempire.servermanager.serverinstance.exceptions;

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;

public class ModifyingRunningServerException extends CustomUserErrorException {

    public ModifyingRunningServerException() {
        super("Cannot modify running server");
    }

    public ModifyingRunningServerException(String message) {
        super(message);
    }
}

