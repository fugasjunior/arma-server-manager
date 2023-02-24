package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;

public class MalformedLauncherModPresetFileException extends CustomUserErrorException {

    public MalformedLauncherModPresetFileException() {
        super("The given mod preset HTML file is malformed");
    }

    public MalformedLauncherModPresetFileException(String message) {
        super(message);
    }
}
