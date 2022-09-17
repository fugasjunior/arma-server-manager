package cz.forgottenempire.arma3servergui.services.impl;

import com.google.common.collect.Lists;
import cz.forgottenempire.arma3servergui.model.ModListPreset;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.repositories.ModPresetsRepository;
import cz.forgottenempire.arma3servergui.repositories.WorkshopModRepository;
import cz.forgottenempire.arma3servergui.services.ModPresetsService;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ModPresetsServiceImpl implements ModPresetsService {

    private ModPresetsRepository presetsRepository;
    private WorkshopModRepository modRepository;

    @Override
    public Collection<ModListPreset> getAllPresets() {
        return Lists.newArrayList(presetsRepository.findAll());
    }

    @Override
    public void createOrUpdatePreset(String name, Collection<Long> modIds) {
        ModListPreset preset = presetsRepository.findModListPresetByName(name).orElse(new ModListPreset(name));
        List<WorkshopMod> mods = modIds.stream()
                .map(modRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
        preset.setMods(mods);
        presetsRepository.save(preset);
    }

    @Override
    public void deletePreset(String name) {
        presetsRepository.deleteById(name);
    }

    @Autowired
    public void setPresetsRepository(ModPresetsRepository presetsRepository) {
        this.presetsRepository = presetsRepository;
    }

    @Autowired
    public void setModRepository(WorkshopModRepository modRepository) {
        this.modRepository = modRepository;
    }
}
