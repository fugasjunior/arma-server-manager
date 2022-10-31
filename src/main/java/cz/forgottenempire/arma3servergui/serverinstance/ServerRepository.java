package cz.forgottenempire.arma3servergui.serverinstance;

import cz.forgottenempire.arma3servergui.serverinstance.entities.Server;
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

    @Query(value = """
            SELECT * FROM server s
            JOIN arma3server_active_mods a ON s.id = a.arma3server_id
            WHERE a.active_mods_id = ?1
            UNION
            SELECT * FROM server s
            JOIN dayzserver_active_mods d ON s.id = d.dayzserver_id
            WHERE d.active_mods_id = ?1
            """,
            nativeQuery = true)
    List<Server> findAllByActiveMod(Long modId);
}
