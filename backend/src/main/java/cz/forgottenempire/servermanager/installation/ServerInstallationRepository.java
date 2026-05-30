package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ServerInstallationRepository extends JpaRepository<ServerInstallation, ServerType> {

    List<ServerInstallation> findAllByInstallationStatus(InstallationStatus installationStatus);
}
