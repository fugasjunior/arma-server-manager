package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;

class MalformedLauncherModPresetFileException extends CustomUserErrorException {

    MalformedLauncherModPresetFileException(String message) {
        super(message);
    }
}
