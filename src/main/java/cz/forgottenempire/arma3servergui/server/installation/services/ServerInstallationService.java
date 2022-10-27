package cz.forgottenempire.arma3servergui.server.installation.services;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.server.installation.entities.ServerInstallation;
import java.util.List;

public interface ServerInstallationService {

    List<ServerInstallation> getAllServerInstallations();
    ServerInstallation getServerInstallation(ServerType type);
}
