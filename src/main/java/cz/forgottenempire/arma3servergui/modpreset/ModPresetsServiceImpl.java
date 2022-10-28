package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.Collection;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class ModPresetsServiceImpl implements ModPresetsService {

    private final ModPresetsRepository presetsRepository;

    @Autowired
    public ModPresetsServiceImpl(ModPresetsRepository presetsRepository) {
        this.presetsRepository = presetsRepository;
    }

    @Override
    public Collection<ModPreset> getAllPresets() {
        return presetsRepository.findAll();
    }

    @Override
    public Collection<ModPreset> getAllPresetsForServer(ServerType serverType) {
        return presetsRepository.getAllByServerType(serverType);
    }

    @Override
    public Collection<ModPreset> getAllPresetsContainingMod(WorkshopMod mod) {
        return presetsRepository.getAllByModsContaining(mod);
    }

    @Override
    public Optional<ModPreset> getModPreset(Long id) {
        return presetsRepository.findById(id);
    }

    @Override
    public ModPreset savePreset(ModPreset preset) {
        return presetsRepository.save(preset);
    }

    @Override
    public ModPreset updatePreset(ModPreset preset) {
        return savePreset(preset);
    }

    @Override
    public void deletePreset(Long preset) {
        presetsRepository.deleteById(preset);
    }
}
