package cz.forgottenempire.arma3servergui.server.repositories;

import cz.forgottenempire.arma3servergui.model.ServerSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerSettingsRepository extends JpaRepository<ServerSettings, Long> {

}
