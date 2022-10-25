package cz.forgottenempire.arma3servergui.workshop.services;

import cz.forgottenempire.arma3servergui.system.entities.SteamAuth;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;

import java.util.Collection;

public interface WorkshopInstallerService {
    void installOrUpdateMods(Collection<WorkshopMod> mod);

    void uninstallMod(WorkshopMod mod);
}
