package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.dtos.ServerQuery;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.SteamAuth;

public interface ArmaServerService {
    boolean startServer(ServerSettings settings);

    void shutDownServer();

    boolean restartServer(ServerSettings settings);

    boolean isServerProcessAlive();

    ServerQuery queryServer(ServerSettings settings);

    void updateServer(SteamAuth auth);

    boolean isServerUpdating();
}
