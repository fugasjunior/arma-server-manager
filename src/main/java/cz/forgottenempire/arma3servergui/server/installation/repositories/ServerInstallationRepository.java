package cz.forgottenempire.arma3servergui.server.installation.repositories;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.server.installation.entities.ServerInstallation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServerInstallationRepository extends JpaRepository<ServerInstallation, ServerType> {

}
