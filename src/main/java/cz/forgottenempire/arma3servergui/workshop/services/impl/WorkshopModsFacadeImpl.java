package cz.forgottenempire.arma3servergui.workshop.services.impl;

import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.common.util.SystemUtils;
import cz.forgottenempire.arma3servergui.common.util.SystemUtils.OSType;
import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.server.installation.exceptions.ServerUnsupportedOnOsException;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import cz.forgottenempire.arma3servergui.workshop.exceptions.ModNotConsumedByGameException;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopFileDetailsService;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopInstallerService;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsFacade;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
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

    @Override
    public Optional<WorkshopMod> getMod(long id) {
        return modsService.getMod(id);
    }

    @Override
    public Collection<WorkshopMod> getAllMods() {
        return modsService.getAllMods();
    }

    @Override
    public Collection<WorkshopMod> getAllMods(@Nullable ServerType filter) {
        if (filter == null) {
            return getAllMods();
        }
        return modsService.getAllMods(filter);
    }

    @Override
    public List<WorkshopMod> saveAndInstallMods(List<Long> ids) {

        List<WorkshopMod> workshopMods = ids.stream()
                .map(id -> getMod(id).orElse(new WorkshopMod(id)))
                .peek(this::setModServerType)
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

    private void setModServerType(WorkshopMod mod) {
        Long appId = fileDetailsService.getModAppId(mod.getId());
        if (Constants.GAME_IDS.get(ServerType.ARMA3).equals(appId)) {
            mod.setServerType(ServerType.ARMA3);
        } else if (Constants.GAME_IDS.get(ServerType.DAYZ).equals(appId)) {
            if (SystemUtils.getOsType() == OSType.LINUX) {
                throw new ServerUnsupportedOnOsException("DayZ mod cannot be installed because DayZ is not supported "
                        + "on Linux servers");
            }

            mod.setServerType(ServerType.DAYZ);
        } else {
            throw new ModNotConsumedByGameException(
                    "The mod " + mod.getId() + " is not consumed by any supported game");
        }
    }
}
