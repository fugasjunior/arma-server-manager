package cz.forgottenempire.arma3servergui.workshop.services.impl;

import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import cz.forgottenempire.arma3servergui.workshop.repositories.WorkshopModRepository;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsService;
import java.util.Collection;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkshopModsServiceImpl implements WorkshopModsService {

    private final WorkshopModRepository modRepository;

    @Autowired
    public WorkshopModsServiceImpl(WorkshopModRepository modRepository) {
        this.modRepository = modRepository;
    }

    @Override
    public Collection<WorkshopMod> getAllMods() {
        return modRepository.findAll();
    }

    @Override
    public Optional<WorkshopMod> getMod(Long id) {
        return modRepository.findById(id);
    }

    @Override
    public WorkshopMod saveMod(WorkshopMod mod) {
        throw new NotImplementedException();
    }

    @Override
    public WorkshopMod deleteMod(WorkshopMod mod) {
        throw new NotImplementedException();
    }

    // TODO extract to separate service or call WorkshopInstallerService directly
//    public WorkshopMod installOrUpdateMod(Long id) {
//        WorkshopMod mod = modRepository.findById(id)
//                .orElseGet(() -> {
//                    if (!isModConsumedByArma3(id)) {
//                        throw new IllegalArgumentException("Mod id " + id + " is not consumed by Arma 3");
//                    }
//                    WorkshopMod newMod = new WorkshopMod(id);
//                    newMod.setName(fileDetailsService.getModName(id));
//                    return modRepository.save(newMod);
//                });
//
//        installerService.installOrUpdateMods(List.of(mod));
//        return mod;
//    }
//
//    public void uninstallMod(Long id) {
//        modRepository.findById(id).ifPresent(mod -> {
//            mod.getServers().forEach(server -> {
//                server.getActiveMods().remove(mod);
//                serverRepository.save(server);
//            });
//            installerService.uninstallMod(mod);
//            modRepository.delete(mod);
//        });
//    }
//
//    private boolean isModConsumedByArma3(Long modId) {
//        Long consumerAppId = fileDetailsService.getModAppId(modId);
//        return Constants.STEAM_ARMA3_ID.equals(consumerAppId);
//    }
}
