package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class WorkshopModsFacade {

    private final WorkshopModsService modsService;
    private final WorkshopInstallerService installerService;

    @Autowired
    public WorkshopModsFacade(
            WorkshopModsService modsService,
            WorkshopInstallerService installerService) {
        this.modsService = modsService;
        this.installerService = installerService;
    }

    public Optional<WorkshopMod> getMod(long id) {
        return modsService.getMod(id);
    }

    public Collection<WorkshopMod> getAllMods() {
        return modsService.getAllMods();
    }

    public Collection<WorkshopMod> getAllMods(@Nullable ServerType filter) {
        if (filter == null) {
            return getAllMods();
        }
        if (filter == ServerType.DAYZ_EXP) {
            filter = ServerType.DAYZ;
        }
        return modsService.getAllMods(filter);
    }

    @Transactional
    public List<WorkshopMod> saveAndInstallMods(List<Long> ids) {
        List<WorkshopMod> workshopMods = ids.stream()
                .map(id -> getMod(id).orElse(new WorkshopMod(id)))
                .toList();

        workshopMods.forEach(mod -> {
            mod.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);
            mod.setErrorStatus(null);
        });
        modsService.saveAllModsForInstallation(workshopMods);

        installerService.installOrUpdateMods(workshopMods);
        return workshopMods;
    }

    @Transactional
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

    public void setModServerOnly(WorkshopMod mod, boolean serverOnly) {
        mod.setServerOnly(serverOnly);
        modsService.saveMod(mod);
    }
}
