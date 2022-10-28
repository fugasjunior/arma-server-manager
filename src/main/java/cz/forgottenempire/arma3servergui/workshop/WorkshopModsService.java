package cz.forgottenempire.arma3servergui.workshop;

import cz.forgottenempire.arma3servergui.common.ServerType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

interface WorkshopModsService {

    Collection<WorkshopMod> getAllMods();

    Collection<WorkshopMod> getAllMods(ServerType filter);

    Optional<WorkshopMod> getMod(Long id);

    WorkshopMod saveMod(WorkshopMod mod);

    List<WorkshopMod> saveAllMods(List<WorkshopMod> mod);

    void deleteMod(WorkshopMod mod);
}
