package cz.forgottenempire.arma3servergui.serverinstance;

import cz.forgottenempire.arma3servergui.serverinstance.entities.Server;
import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;

interface ServerInstanceService {

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
