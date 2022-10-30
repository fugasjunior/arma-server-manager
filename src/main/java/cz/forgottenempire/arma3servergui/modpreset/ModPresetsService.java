package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModPresetsService {

    private final ModPresetsRepository presetsRepository;

    @Autowired
    public ModPresetsService(ModPresetsRepository presetsRepository) {
        this.presetsRepository = presetsRepository;
    }

    public Collection<ModPreset> getAllPresets() {
        return presetsRepository.findAll();
    }

    public Collection<ModPreset> getAllPresetsForServer(ServerType serverType) {
        return presetsRepository.getAllByType(serverType);
    }

    public Collection<ModPreset> getAllPresetsContainingMod(WorkshopMod mod) {
        return presetsRepository.getAllByModsContaining(mod);
    }

    public Optional<ModPreset> getModPreset(Long id) {
        return presetsRepository.findById(id);
    }

    public ModPreset savePreset(ModPreset preset) {
        return presetsRepository.save(preset);
    }

    public ModPreset updatePreset(ModPreset preset) {
        return savePreset(preset);
    }

    public void deletePreset(Long preset) {
        presetsRepository.deleteById(preset);
    }

    public boolean presetWithNameExists(String name) {
        return presetsRepository.existsByName(name);
    }
}
