package cz.forgottenempire.arma3servergui.server.installation.services;

import cz.forgottenempire.arma3servergui.server.ServerType;

public interface ServerInstallerService {

    void queueServerInstallation(ServerType serverType);

    boolean isServerInstalled(ServerType serverType);

    boolean isServerBeingUpdated(ServerType serverType);

}
