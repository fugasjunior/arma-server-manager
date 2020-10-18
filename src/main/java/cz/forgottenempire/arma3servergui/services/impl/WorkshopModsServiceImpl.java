package cz.forgottenempire.arma3servergui.services.impl;

import com.google.common.collect.Lists;
import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.repositories.WorkshopModRepository;
import cz.forgottenempire.arma3servergui.services.SteamAuthService;
import cz.forgottenempire.arma3servergui.services.WorkshopFileDetailsService;
import cz.forgottenempire.arma3servergui.services.WorkshopInstallerService;
import cz.forgottenempire.arma3servergui.services.WorkshopModsService;
import java.util.Collection;
import java.util.NoSuchElementException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkshopModsServiceImpl implements WorkshopModsService {

    private SteamAuthService authService;
    private WorkshopInstallerService installerService;
    private WorkshopFileDetailsService fileDetailsService;

    private WorkshopModRepository modRepository;

    @Override
    public Collection<WorkshopMod> getAllMods() {
        return Lists.newArrayList(modRepository.findAll());
    }

    @Override
    public WorkshopMod installOrUpdateMod(Long id) {
        WorkshopMod mod = modRepository.findById(id)
                .orElseGet(() -> {
                    if (!isModConsumedByArma3(id)) {
                        throw new IllegalArgumentException("Mod id " + id + " is not consumed by Arma 3");
                    }
                    WorkshopMod newMod = new WorkshopMod(id);
                    newMod.setName(fileDetailsService.getModName(id));
                    return modRepository.save(newMod);
                });

        installerService.installOrUpdateMod(getAuth(), mod);
        return mod;
    }

    @Override
    public void uninstallMod(Long id) {
        modRepository.findById(id).ifPresent(installerService::deleteMod);
    }

    @Override
    public void activateMod(Long id, boolean active) {
        WorkshopMod mod = modRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        mod.setActive(active);
        modRepository.save(mod);
    }

    @Override
    public void updateAllMods() {
        installerService.updateAllMods(getAuth());
    }

    private boolean isModConsumedByArma3(Long modId) {
        Long consumerAppId = fileDetailsService.getModAppId(modId);
        return Constants.STEAM_ARMA3_ID.equals(consumerAppId);
    }

    private SteamAuth getAuth() {
        return authService.getAuthAccount();
    }

    @Autowired
    public void setAuthService(SteamAuthService authService) {
        this.authService = authService;
    }

    @Autowired
    public void setInstallerService(WorkshopInstallerService installerService) {
        this.installerService = installerService;
    }

    @Autowired
    public void setFileDetailsService(WorkshopFileDetailsService fileDetailsService) {
        this.fileDetailsService = fileDetailsService;
    }

    @Autowired
    public void setModRepository(WorkshopModRepository modRepository) {
        this.modRepository = modRepository;
    }
}
