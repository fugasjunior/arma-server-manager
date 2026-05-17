package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;
import org.springframework.http.HttpStatus;

class InstallationInProgressException extends CustomUserErrorException {

    InstallationInProgressException(ServerType type) {
        super("Installation of server '" + type + "' is currently in progress", HttpStatus.CONFLICT);
    }
}
