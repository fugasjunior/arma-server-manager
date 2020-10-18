package cz.forgottenempire.arma3servergui.repositories;

import cz.forgottenempire.arma3servergui.model.ServerSettings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerSettingsRepository extends CrudRepository<ServerSettings, Long> {

}
