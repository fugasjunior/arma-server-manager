package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface ModPresetsRepository extends JpaRepository<ModPreset, Long> {

    Collection<ModPreset> getAllByModsContaining(WorkshopMod mod);

    Collection<ModPreset> getAllByServerType(ServerType serverType);

}
