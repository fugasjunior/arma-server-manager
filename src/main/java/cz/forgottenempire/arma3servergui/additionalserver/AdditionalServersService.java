package cz.forgottenempire.arma3servergui.additionalserver;

import java.util.List;
import java.util.Optional;

interface AdditionalServersService {

    List<AdditionalServer> getAllServers();

    Optional<AdditionalServer> getServer(Long id);

    AdditionalServerInstanceInfo getServerInstanceInfo(Long id);

    void startServer(Long serverId);

    void stopServer(Long serverId);
}
