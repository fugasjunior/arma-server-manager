package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.common.ServerType;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "mod type differs from preset")
public class ModTypeDiffersFromPresetException extends RuntimeException {

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
