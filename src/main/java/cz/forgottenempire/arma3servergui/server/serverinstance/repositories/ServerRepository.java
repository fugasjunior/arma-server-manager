package cz.forgottenempire.arma3servergui.server.serverinstance.repositories;

import cz.forgottenempire.arma3servergui.server.serverinstance.entities.Server;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends CrudRepository<Server, Long> {

    @Nonnull List<Server> findAll();
    @Nonnull List<Server> findAllByPortOrQueryPort(int port, int queryPort);

}
