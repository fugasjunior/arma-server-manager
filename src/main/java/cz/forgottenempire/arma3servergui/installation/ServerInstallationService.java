package cz.forgottenempire.arma3servergui.installation;

import cz.forgottenempire.arma3servergui.common.ServerType;
import java.util.List;

interface ServerInstallationService {

    List<ServerInstallation> getAllServerInstallations();

    ServerInstallation getServerInstallation(ServerType type);
}
