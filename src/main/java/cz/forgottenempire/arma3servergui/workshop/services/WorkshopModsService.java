package cz.forgottenempire.arma3servergui.workshop.services;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WorkshopModsService {

    Collection<WorkshopMod> getAllMods();

    Collection<WorkshopMod> getAllMods(ServerType filter);

    Optional<WorkshopMod> getMod(Long id);

    WorkshopMod saveMod(WorkshopMod mod);

    List<WorkshopMod> saveAllMods(List<WorkshopMod> mod);

    void deleteMod(WorkshopMod mod);
}
