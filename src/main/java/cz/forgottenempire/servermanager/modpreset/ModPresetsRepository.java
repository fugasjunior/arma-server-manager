package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ModPresetsRepository extends JpaRepository<ModPreset, Long> {

    Collection<ModPreset> getAllByModsContaining(WorkshopMod mod);

    Collection<ModPreset> getAllByType(ServerType serverType);

    boolean existsByName(String name);
}
