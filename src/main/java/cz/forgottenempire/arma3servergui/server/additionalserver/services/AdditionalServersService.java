package cz.forgottenempire.arma3servergui.server.additionalserver.services;

import cz.forgottenempire.arma3servergui.server.additionalserver.AdditionalServerInstanceInfo;
import cz.forgottenempire.arma3servergui.server.additionalserver.entities.AdditionalServer;
import java.util.List;
import java.util.Optional;

public interface AdditionalServersService {

    List<AdditionalServer> getAllServers();

    Optional<AdditionalServer> getServer(Long id);

    AdditionalServerInstanceInfo getServerInstanceInfo(Long id);

    void startServer(Long serverId);

    void stopServer(Long serverId);
}
