package cz.forgottenempire.arma3servergui.common;

import java.util.List;

public enum ServerType {
    ARMA3,
    DAYZ,
    DAYZ_EXP,
    REFORGER;

    public static List<ServerType> getAll() {
        return List.of(ARMA3, DAYZ, DAYZ_EXP, REFORGER);
    }
}
