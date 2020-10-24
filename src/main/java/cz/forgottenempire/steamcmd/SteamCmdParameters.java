package cz.forgottenempire.steamcmd;

import java.util.ArrayList;
import java.util.List;

public class SteamCmdParameters {

    private final List<String> parameters;

    protected SteamCmdParameters() {
        parameters = new ArrayList<>();
    }

    public List<String> getParameters() {
        return new ArrayList<>(parameters);
    }

    protected void addParameter(String parameter) {
        parameters.add(parameter);
    }
}
