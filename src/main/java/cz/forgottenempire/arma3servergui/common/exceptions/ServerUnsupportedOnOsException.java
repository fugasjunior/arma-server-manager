package cz.forgottenempire.arma3servergui.common.exceptions;

import cz.forgottenempire.arma3servergui.common.ServerType;

public class ServerUnsupportedOnOsException extends CustomUserErrorException {

    public ServerUnsupportedOnOsException() {
        super("This game is not supported on the server OS");
    }

    public ServerUnsupportedOnOsException(String message) {
        super(message);
    }

    public ServerUnsupportedOnOsException(ServerType serverType) {
        super("Server '" + serverType + "' is not supported on the server OS");
    }
}
