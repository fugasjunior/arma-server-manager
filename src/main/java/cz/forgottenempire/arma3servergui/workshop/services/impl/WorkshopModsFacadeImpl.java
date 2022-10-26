package cz.forgottenempire.arma3servergui.workshop.services.impl;

import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopFileDetailsService;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopInstallerService;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsFacade;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WorkshopModsFacadeImpl implements WorkshopModsFacade {

    private final WorkshopModsService modsService;
    private final WorkshopInstallerService installerService;
    private final WorkshopFileDetailsService fileDetailsService;

    @Autowired
    public WorkshopModsFacadeImpl(
            WorkshopModsService modsService,
            WorkshopInstallerService installerService,
            WorkshopFileDetailsService fileDetailsService
    ) {
        this.modsService = modsService;
        this.installerService = installerService;
        this.fileDetailsService = fileDetailsService;
    }

    public Optional<WorkshopMod> getMod(long id) {
        return modsService.getMod(id);
    }

    public Collection<WorkshopMod> getAllMods() {
        return modsService.getAllMods();
    }

    public List<WorkshopMod> saveAndInstallMods(List<Long> ids) {
        throw new NotImplementedException();
    }

    @Override
    public void updateAllMods() {
        throw new NotImplementedException();
    }

    public void uninstallMod(long id) {
        throw new NotImplementedException();
    }
}
