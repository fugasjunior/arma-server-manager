package cz.forgottenempire.arma3servergui.services;

import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;

import java.util.Collection;

public interface SteamCmdService {
    boolean installOrUpdateMod(SteamAuth auth, WorkshopMod mod);
    boolean deleteMod(WorkshopMod mod);
    boolean refreshMods(SteamAuth auth);
}
