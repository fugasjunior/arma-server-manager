package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;

class ModNotConsumedByGameException extends CustomUserErrorException {

    public ModNotConsumedByGameException() {
        super("The given mod is not consumed by the selected game");
    }

    public ModNotConsumedByGameException(String message) {
        super(message);
    }
}
