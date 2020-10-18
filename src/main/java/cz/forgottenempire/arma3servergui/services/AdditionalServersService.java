package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.model.AdditionalServer;
import java.util.Collection;

public interface AdditionalServersService {
    void startServer(Long serverId);

    void stopServer(Long serverId);

    boolean isAlive(Long serverId);

    Collection<AdditionalServer> getAllServers();
}
