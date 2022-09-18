package cz.forgottenempire.arma3servergui.workshop.services.services;

import cz.forgottenempire.arma3servergui.workshop.entities.ModListPreset;
import java.util.Collection;

public interface ModPresetsService {

    Collection<ModListPreset> getAllPresets();

    void createOrUpdatePreset(String name, Collection<Long> modIds);

    void deletePreset(String name);
}
