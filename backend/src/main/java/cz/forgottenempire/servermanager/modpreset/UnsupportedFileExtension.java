package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;

public class UnsupportedFileExtension extends CustomUserErrorException {

    public UnsupportedFileExtension() {
        super("Only HTML files are allowed");
    }
}
