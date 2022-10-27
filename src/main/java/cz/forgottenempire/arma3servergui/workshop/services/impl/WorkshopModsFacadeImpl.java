package cz.forgottenempire.arma3servergui.workshop.services.impl;

import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import cz.forgottenempire.arma3servergui.workshop.exceptions.ModNotConsumedByGameException;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopFileDetailsService;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopInstallerService;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsFacade;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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
        List<WorkshopMod> workshopMods = ids.stream()
                .peek(id -> validateModConsumedByGameId(id, Constants.STEAM_ARMA3_ID))
                .map(id -> getMod(id).orElse(new WorkshopMod(id)))
                .toList();

        workshopMods.forEach(this::setModNameFromWorkshop);
        modsService.saveAllMods(workshopMods);

        installerService.installOrUpdateMods(workshopMods);
        return workshopMods;
    }

    @Override
    public void updateAllMods() {
        List<Long> allModIds = modsService.getAllMods().stream()
                .map(WorkshopMod::getId)
                .toList();
        saveAndInstallMods(allModIds);
    }

    public void uninstallMod(long id) {
        WorkshopMod workshopMod = getMod(id)
                .orElseThrow(() -> new NotFoundException("Mod ID " + id + " not found."));
        installerService.uninstallMod(workshopMod);
        modsService.deleteMod(workshopMod);
    }

    private void setModNameFromWorkshop(WorkshopMod mod) {
        mod.setName(fileDetailsService.getModName(mod.getId()));
    }

    private void validateModConsumedByGameId(long modId, long gameId) {
        if (gameId != fileDetailsService.getModAppId(modId)) {
            throw new ModNotConsumedByGameException("The mod " + modId + " is not consumed by game " + gameId);
        }
    }
}
