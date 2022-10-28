package cz.forgottenempire.arma3servergui.server.serverinstance.services;

import cz.forgottenempire.arma3servergui.server.ServerInstanceInfo;
import cz.forgottenempire.arma3servergui.server.serverinstance.entities.Server;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

public interface ServerInstanceService {

    List<Server> getAllServers();

    Optional<Server> getServer(@NotNull Long id);

    Server createServer(Server server);

    Server updateServer(Server server);

    void deleteServer(Server server);

    void startServer(@NotNull Long id);

    void shutDownServer(@NotNull Long id);

    void restartServer(@NotNull Long id);

    ServerInstanceInfo getServerInstanceInfo(@NotNull Long id);
}
