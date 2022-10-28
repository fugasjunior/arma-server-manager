package cz.forgottenempire.arma3servergui.installation;

import cz.forgottenempire.arma3servergui.common.ServerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ServerInstallationRepository extends JpaRepository<ServerInstallation, ServerType> {

}
