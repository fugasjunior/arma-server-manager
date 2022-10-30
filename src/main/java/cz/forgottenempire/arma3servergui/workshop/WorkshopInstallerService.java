package cz.forgottenempire.arma3servergui.workshop;

import java.util.Collection;

interface WorkshopInstallerService {

    void installOrUpdateMods(Collection<WorkshopMod> mod);

    void uninstallMod(WorkshopMod mod);
}
