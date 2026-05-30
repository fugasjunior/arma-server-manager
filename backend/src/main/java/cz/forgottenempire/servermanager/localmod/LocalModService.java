package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocalModService {

    private final LocalModRepository repository;

    @Autowired
    public LocalModService(LocalModRepository repository) {
        this.repository = repository;
    }

    public List<LocalMod> getAllMods() {
        return repository.findAll();
    }

    public List<LocalMod> getAllMods(ServerType filter) {
        return repository.findAllByServerType(filter);
    }

    public Optional<LocalMod> getMod(Long id) {
        return repository.findById(id);
    }

    public LocalMod saveMod(LocalMod mod) {
        return repository.save(mod);
    }

    public LocalMod requireMod(Long id) {
        return getMod(id).orElseThrow(() -> new NotFoundException("Local mod ID " + id + " not found"));
    }

    public void deleteMod(Long id) {
        repository.deleteById(id);
    }

    public List<String> getNamesForServerType(ServerType serverType) {
        return getAllMods(serverType).stream().map(LocalMod::getName).toList();
    }
}
