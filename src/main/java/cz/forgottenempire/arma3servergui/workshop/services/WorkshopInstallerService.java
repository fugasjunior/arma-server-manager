package cz.forgottenempire.arma3servergui.workshop.services;

import cz.forgottenempire.arma3servergui.workshop.entities.SteamAuth;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;

public interface WorkshopInstallerService {
    void installOrUpdateMod(SteamAuth auth, WorkshopMod mod);

    boolean deleteMod(WorkshopMod mod);

    boolean updateAllMods(SteamAuth auth);
}
