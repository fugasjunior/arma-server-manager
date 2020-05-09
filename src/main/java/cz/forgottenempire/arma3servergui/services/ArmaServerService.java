package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.dtos.ServerStatus;
import cz.forgottenempire.arma3servergui.model.ServerSettings;

public interface ArmaServerService {
    boolean startServer(ServerSettings settings);

    boolean shutDownServer(ServerSettings settings);

    ServerStatus getServerStatus(ServerSettings settings);
}
