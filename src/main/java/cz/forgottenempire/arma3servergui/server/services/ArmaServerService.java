package cz.forgottenempire.arma3servergui.server.services;

import cz.forgottenempire.arma3servergui.server.entities.Server;
import cz.forgottenempire.arma3servergui.workshop.entities.SteamAuth;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

public interface ArmaServerService {

    List<Server> getAllServers();

    Optional<Server> getServer(@NotNull Long id);

    Server createServer(Server server);

    Server updateServer(Server server);

    void deleteServer(Server server);

    boolean startServer(Server settings);

    void shutDownServer();

    boolean restartServer(Server settings);

    boolean isServerProcessAlive();

    void updateServer(SteamAuth auth);

    boolean isServerUpdating();
}
