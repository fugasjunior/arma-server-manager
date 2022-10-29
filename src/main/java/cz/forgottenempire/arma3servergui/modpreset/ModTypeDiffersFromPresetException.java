package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.common.exceptions.CustomUserErrorException;

public class ModTypeDiffersFromPresetException extends CustomUserErrorException {

    public ModTypeDiffersFromPresetException() {
        super("Mod type differs from preset type");
    }

    public ModTypeDiffersFromPresetException(String message) {
        super(message);
    }

    public ModTypeDiffersFromPresetException(Long modId, ServerType modType, ServerType presetType) {
        super("Mod ID %d of type '%s' differs from mod preset type '%s'".formatted(modId, modType, presetType));
    }
}
