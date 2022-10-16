package cz.forgottenempire.arma3servergui.workshop.services;

import cz.forgottenempire.arma3servergui.workshop.entities.ModListPreset;
import java.util.Collection;

public interface ModPresetsService {

    Collection<ModListPreset> getAllPresets();

    void createOrUpdatePreset(String name, Collection<Long> modIds);

    // TODO change this to preset ID
    void deletePreset(String name);
}
