package cz.forgottenempire.servermanager.serverinstance.exceptions;

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;

public class PortAlreadyTakenException extends CustomUserErrorException {

    public PortAlreadyTakenException() {
        super("The selected port is already in use");
    }

    public PortAlreadyTakenException(String message) {
        super(message);
    }
}

