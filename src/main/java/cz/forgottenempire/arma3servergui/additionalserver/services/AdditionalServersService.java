package cz.forgottenempire.arma3servergui.additionalserver.services;

import cz.forgottenempire.arma3servergui.additionalserver.entities.AdditionalServer;
import java.util.Collection;
import java.util.Optional;

public interface AdditionalServersService {
    void startServer(Long serverId);

    void stopServer(Long serverId);

    boolean isAlive(Long serverId);

    Optional<AdditionalServer> getServer(Long id);

    Collection<AdditionalServer> getAllServers();
}
