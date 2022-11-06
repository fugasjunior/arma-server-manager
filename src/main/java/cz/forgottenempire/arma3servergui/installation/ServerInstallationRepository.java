package cz.forgottenempire.arma3servergui.installation;

import cz.forgottenempire.arma3servergui.common.ServerType;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

@Repository
interface ServerInstallationRepository extends JpaRepository<ServerInstallation, ServerType> {

    @Override
    @CacheEvict(value = "serverInstallationsResponse", allEntries = true)
    @NonNull
    <S extends ServerInstallation> S save(@NonNull S entity);
}
