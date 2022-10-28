package cz.forgottenempire.arma3servergui.workshop;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "mod is not consumed by game")
class ModNotConsumedByGameException extends RuntimeException {

    public ModNotConsumedByGameException() {
    }

    public ModNotConsumedByGameException(String message) {
        super(message);
    }
}
