package cz.forgottenempire.arma3servergui.additionalserver.services;

import cz.forgottenempire.arma3servergui.additionalserver.AdditionalServerInstanceInfo;
import cz.forgottenempire.arma3servergui.model.AdditionalServer;
import java.util.List;
import java.util.Optional;

public interface AdditionalServersService {

    List<AdditionalServer> getAllServers();

    Optional<AdditionalServer> getServer(Long id);

    AdditionalServerInstanceInfo getServerInstanceInfo(Long id);

    void startServer(Long serverId);

    void stopServer(Long serverId);
}
