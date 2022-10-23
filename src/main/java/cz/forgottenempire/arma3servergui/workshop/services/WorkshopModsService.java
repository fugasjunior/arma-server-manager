package cz.forgottenempire.arma3servergui.workshop.services;

import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.Collection;
import java.util.Optional;

public interface WorkshopModsService {

    Collection<WorkshopMod> getAllMods();

    Optional<WorkshopMod> getMod(Long id);

    WorkshopMod installOrUpdateMod(Long id);

    void uninstallMod(Long id);

    void updateAllMods();

}
