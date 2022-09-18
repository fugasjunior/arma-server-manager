package cz.forgottenempire.arma3servergui.workshop.services;

import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.Collection;

public interface WorkshopModsService {

    Collection<WorkshopMod> getAllMods();

    WorkshopMod installOrUpdateMod(Long id);

    void uninstallMod(Long id);

//    void activateMod(Long id, boolean active);

    void updateAllMods();

}
