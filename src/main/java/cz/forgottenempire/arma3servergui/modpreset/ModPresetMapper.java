package cz.forgottenempire.arma3servergui.modpreset;

import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
interface ModPresetMapper {

    ModPresetDto mapToModPresetDto(ModPreset modPreset);

    ModPreset mapModPresetDtoToEntity(ModPresetDto modPresetDto);

    List<ModPresetDto> mapToModPresetDtos(Collection<ModPreset> modPresets);

    void updateModPresetFromDto(@MappingTarget ModPreset modPreset, ModPresetDto modPresetDto);
}
