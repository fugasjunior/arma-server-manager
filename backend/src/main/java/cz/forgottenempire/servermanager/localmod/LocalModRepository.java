package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.common.ServerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface LocalModRepository extends JpaRepository<LocalMod, Long> {

    List<LocalMod> findAllByServerType(ServerType serverType);

    Optional<LocalMod> findByServerTypeAndName(ServerType serverType, String name);
}
