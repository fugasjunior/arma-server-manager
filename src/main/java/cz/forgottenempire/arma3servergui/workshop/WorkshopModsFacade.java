package cz.forgottenempire.arma3servergui.workshop;

import cz.forgottenempire.arma3servergui.common.ServerType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface WorkshopModsFacade {

    Optional<WorkshopMod> getMod(long id);

    Collection<WorkshopMod> getAllMods();

    Collection<WorkshopMod> getAllMods(ServerType filter);

    List<WorkshopMod> saveAndInstallMods(List<Long> ids);

    void updateAllMods();

    void uninstallMod(long id);
}
