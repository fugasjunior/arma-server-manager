package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.modpreset.ModPresetsService;
import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.DayZServer;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkshopModsService {

    private final WorkshopModRepository modRepository;
    private final ServerRepository serverRepository;
    private final ModPresetsService modPresetsService;

    @Autowired
    public WorkshopModsService(WorkshopModRepository modRepository, ServerRepository serverRepository,
            ModPresetsService modPresetsService) {
        this.modRepository = modRepository;
        this.serverRepository = serverRepository;
        this.modPresetsService = modPresetsService;
    }

    public Collection<WorkshopMod> getAllMods() {
        return modRepository.findAll();
    }

    public Collection<WorkshopMod> getAllMods(ServerType filter) {
        return modRepository.findAllByServerType(filter);
    }

    public Optional<WorkshopMod> getMod(Long id) {
        return modRepository.findById(id);
    }

    public WorkshopMod saveMod(WorkshopMod mod) {
        return modRepository.save(mod);
    }

    public void saveModForInstallation(WorkshopMod mod) {
        modRepository.save(mod);
        Hibernate.initialize(mod.getBiKeys());
    }

    public List<WorkshopMod> saveAllMods(List<WorkshopMod> mods) {
        return modRepository.saveAll(mods);
    }

    public void deleteMod(WorkshopMod mod) {
        removeModFromPresets(mod);
        removeModFromServers(mod);
        modRepository.delete(mod);
    }

    private void removeModFromPresets(WorkshopMod mod) {
        modPresetsService.getAllPresetsContainingMod(mod).forEach(preset -> {
            List<WorkshopMod> modsInPreset = preset.getMods();
            if (modsInPreset.size() == 1) {
                modPresetsService.deletePreset(preset.getId());
            } else {
                modsInPreset.remove(mod);
                modPresetsService.savePreset(preset);
            }
        });
    }

    private void removeModFromServers(WorkshopMod mod) {
        serverRepository.findAllServerIdsByActiveMod(mod.getId())
                .forEach(serverId -> {
                    Server server = serverRepository.findById(serverId).orElseThrow();
                    if (server instanceof Arma3Server arma3Server) {
                        arma3Server.getActiveMods().remove(mod);
                    } else if (server instanceof DayZServer dayZServer) {
                        dayZServer.getActiveMods().remove(mod);
                    }
                    serverRepository.save(server);
                });
    }
}
