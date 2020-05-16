package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.dtos.ServerQuery;
import cz.forgottenempire.arma3servergui.model.ServerSettings;

public interface ArmaServerService {
    boolean startServer(ServerSettings settings);

    boolean shutDownServer(ServerSettings settings);

    boolean restartServer(ServerSettings settings);

    boolean isServerProcessAlive();

    ServerQuery queryServer(ServerSettings settings);
}
