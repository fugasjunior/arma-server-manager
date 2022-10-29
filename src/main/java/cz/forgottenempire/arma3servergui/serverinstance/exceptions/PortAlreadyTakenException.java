package cz.forgottenempire.arma3servergui.serverinstance.exceptions;

import cz.forgottenempire.arma3servergui.common.exceptions.CustomUserErrorException;

public class PortAlreadyTakenException extends CustomUserErrorException {

    public PortAlreadyTakenException() {
        super("The selected port is already in use");
    }

    public PortAlreadyTakenException(String message) {
        super(message);
    }
}

