package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.serverinstance.entities.Server;
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
            SELECT arma3server_id FROM arma3server_active_mods
            WHERE active_mods_id = ?1
            UNION
            SELECT dayzserver_id FROM dayzserver_active_mods
            WHERE active_mods_id = ?1
            """,
            nativeQuery = true)
    List<Long> findAllServerIdsByActiveMod(Long modId);
}
