package cz.forgottenempire.servermanager.common.exceptions;

import cz.forgottenempire.servermanager.common.ServerType;

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
