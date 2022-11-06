package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.common.exceptions.ServerNotInitializedException;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class WorkshopModsFacade {

    private final WorkshopModsService modsService;
    private final WorkshopInstallerService installerService;
    private final WorkshopFileDetailsService fileDetailsService;
    private final ServerInstallationService serverInstallationService;

    @Autowired
    public WorkshopModsFacade(
            WorkshopModsService modsService,
            WorkshopInstallerService installerService,
            WorkshopFileDetailsService fileDetailsService,
            ServerInstallationService serverInstallationService) {
        this.modsService = modsService;
        this.installerService = installerService;
        this.fileDetailsService = fileDetailsService;
        this.serverInstallationService = serverInstallationService;
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

    public List<WorkshopMod> saveAndInstallMods(List<Long> ids) {

        List<WorkshopMod> workshopMods = ids.stream()
                .map(id -> getMod(id).orElse(new WorkshopMod(id)))
                .peek(this::setModServerType)
                .peek(this::validateServerInitialized)
                .toList();

        workshopMods.forEach(this::setModNameFromWorkshop);
        modsService.saveAllMods(workshopMods);

        installerService.installOrUpdateMods(workshopMods);
        return workshopMods;
    }

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
            mod.setServerType(ServerType.DAYZ);
        } else {
            log.warn("Tried to install mod ID {} which is not consumed by any of the supported servers", mod.getId());
            throw new ModNotConsumedByGameException(
                    "The mod " + mod.getId() + " is not consumed by any supported game");
        }
    }

    private void validateServerInitialized(WorkshopMod workshopMod) {
        if (workshopMod.getServerType() == ServerType.ARMA3 && isServerNotInitialized(ServerType.ARMA3)) {
            throw new ServerNotInitializedException("Mod installation failed: Arma 3 server is not installed");
        }
        if (workshopMod.getServerType() == ServerType.DAYZ && isServerNotInitialized(ServerType.DAYZ)
                && isServerNotInitialized(ServerType.DAYZ_EXP)) {
            throw new ServerNotInitializedException(
                    "Mod installation failed: Neither DayZ nor DayZ Experimental server is installed");
        }
    }

    private boolean isServerNotInitialized(ServerType serverType) {
        return !serverInstallationService.isServerInstalled(serverType);
    }
}
