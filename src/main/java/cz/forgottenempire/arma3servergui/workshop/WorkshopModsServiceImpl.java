package cz.forgottenempire.arma3servergui.workshop;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.serverinstance.ServerRepository;
import cz.forgottenempire.arma3servergui.serverinstance.entities.Arma3Server;
import cz.forgottenempire.arma3servergui.serverinstance.entities.DayZServer;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class WorkshopModsServiceImpl implements WorkshopModsService {

    private final WorkshopModRepository modRepository;
    private final ServerRepository serverRepository;

    @Autowired
    public WorkshopModsServiceImpl(WorkshopModRepository modRepository, ServerRepository serverRepository) {
        this.modRepository = modRepository;
        this.serverRepository = serverRepository;
    }

    @Override
    public Collection<WorkshopMod> getAllMods() {
        return modRepository.findAll();
    }

    @Override
    public Collection<WorkshopMod> getAllMods(ServerType filter) {
        return modRepository.findAllByServerType(filter);
    }

    @Override
    public Optional<WorkshopMod> getMod(Long id) {
        return modRepository.findById(id);
    }

    @Override
    public WorkshopMod saveMod(WorkshopMod mod) {
        return modRepository.save(mod);
    }

    @Override
    public List<WorkshopMod> saveAllMods(List<WorkshopMod> mods) {
        return modRepository.saveAll(mods);
    }

    @Override
    public void deleteMod(WorkshopMod mod) {
        serverRepository.findAllByActiveMod(mod.getId())
                .forEach(server -> {
                    if (server instanceof Arma3Server arma3Server) {
                        arma3Server.getActiveMods().remove(mod);
                    } else if (server instanceof DayZServer dayZServer) {
                        dayZServer.getActiveMods().remove(mod);
                    }
                    serverRepository.save(server);
                });
        modRepository.delete(mod);
    }
}
