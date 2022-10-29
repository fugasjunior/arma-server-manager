package cz.forgottenempire.arma3servergui.modpreset.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PresetListResponseDto {

    List<PresetResponseDto> presets;
}
