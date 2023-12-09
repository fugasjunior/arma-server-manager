package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface ServerInstallationRepository extends JpaRepository<ServerInstallation, ServerType> {

    @Override
    @CacheEvict(value = "serverInstallationsResponse", allEntries = true)
    @NonNull
    <S extends ServerInstallation> S save(@NonNull S entity);

    List<ServerInstallation> findAllByInstallationStatus(InstallationStatus installationStatus);
}
