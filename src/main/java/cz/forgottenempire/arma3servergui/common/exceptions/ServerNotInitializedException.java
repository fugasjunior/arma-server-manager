package cz.forgottenempire.arma3servergui.common.exceptions;

import cz.forgottenempire.arma3servergui.common.ServerType;

public class ServerNotInitializedException extends CustomUserErrorException {

    public ServerNotInitializedException() {
        this("Server is not initialized");
    }

    public ServerNotInitializedException(String message) {
        super(message);
    }

    public ServerNotInitializedException(ServerType serverType) {
        this("Server '" + serverType + "' is not initialized");
    }
}
