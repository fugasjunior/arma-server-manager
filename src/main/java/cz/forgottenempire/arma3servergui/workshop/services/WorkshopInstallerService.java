package cz.forgottenempire.arma3servergui.workshop.services;

import cz.forgottenempire.arma3servergui.system.entities.SteamAuth;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;

public interface WorkshopInstallerService {
    void installOrUpdateMod(SteamAuth auth, WorkshopMod mod);

    void deleteMod(WorkshopMod mod);

    void updateAllMods(SteamAuth auth);
}
