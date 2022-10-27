package cz.forgottenempire.arma3servergui.server;

import java.util.List;

public enum ServerType {
    ARMA3;
    // TODO support more servers

    public static List<ServerType> getAll() {
        return List.of(ARMA3);
    }
}
