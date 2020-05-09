package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.ServerStatus;

public interface ArmaServerService {
    boolean startServer(ServerSettings settings);

    boolean shutDownServer();

    ServerStatus getServerStatus();
}
