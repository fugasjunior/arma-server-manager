package cz.forgottenempire.arma3servergui.modpreset;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ModPresetsDto {

    List<ModPresetDto> presets;
}
