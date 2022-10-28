package cz.forgottenempire.arma3servergui.server.serverinstance.repositories;

import cz.forgottenempire.arma3servergui.server.serverinstance.entities.Server;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends CrudRepository<Server, Long> {

    @Nonnull
    List<Server> findAll();

    @Nonnull
    List<Server> findAllByPortOrQueryPort(int port, int queryPort);

    @Query(value = "SELECT * FROM server s "
            + "JOIN server_mod sm "
            + "ON s.id = sm.server_id "
            + "WHERE sm.mod_id = ?1",
            nativeQuery = true)
    List<Server> findAllByActiveMod(Long modId);
}
