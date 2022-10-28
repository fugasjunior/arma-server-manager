package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import java.util.Collection;
import java.util.Optional;

public interface ModPresetsService {

    Collection<ModPreset> getAllPresets();

    Collection<ModPreset> getAllPresetsForServer(ServerType serverType);

    Collection<ModPreset> getAllPresetsContainingMod(WorkshopMod mod);

    Optional<ModPreset> getModPreset(Long id);

    ModPreset savePreset(ModPreset preset);

    ModPreset updatePreset(ModPreset preset);

    void deletePreset(Long preset);

}
