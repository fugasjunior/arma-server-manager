package cz.forgottenempire.arma3servergui.workshop;

import cz.forgottenempire.arma3servergui.common.ServerType;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface WorkshopModRepository extends JpaRepository<WorkshopMod, Long> {

    Collection<WorkshopMod> findAllByServerType(ServerType serverType);
}
