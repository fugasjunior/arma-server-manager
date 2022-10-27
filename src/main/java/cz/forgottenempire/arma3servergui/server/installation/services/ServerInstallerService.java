package cz.forgottenempire.arma3servergui.server.installation.services;

import cz.forgottenempire.arma3servergui.server.ServerType;

public interface ServerInstallerService {

    void installServer(ServerType serverType, String betaBranch);
}
