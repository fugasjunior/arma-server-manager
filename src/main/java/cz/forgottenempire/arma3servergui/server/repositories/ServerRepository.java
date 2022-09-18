package cz.forgottenempire.arma3servergui.server.repositories;

import cz.forgottenempire.arma3servergui.server.entities.Server;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerRepository extends JpaRepository<Server, Long> {

}
