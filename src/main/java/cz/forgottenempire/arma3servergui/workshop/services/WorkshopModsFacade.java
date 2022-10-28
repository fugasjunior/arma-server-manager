package cz.forgottenempire.arma3servergui.workshop.services;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorkshopModsFacade {

    Optional<WorkshopMod> getMod(long id);

    Collection<WorkshopMod> getAllMods();

    Collection<WorkshopMod> getAllMods(ServerType filter);

    List<WorkshopMod> saveAndInstallMods(List<Long> ids);

    void updateAllMods();

    void uninstallMod(long id);
}
