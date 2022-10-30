package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.modpreset.dtos.PresetResponseDto;
import cz.forgottenempire.arma3servergui.modpreset.dtos.PresetResponseModDto;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
interface ModPresetMapper {

    PresetResponseDto mapToModPresetDto(ModPreset modPreset);

    List<PresetResponseDto> mapToModPresetDtos(Collection<ModPreset> modPresets);

    @Mapping(target = "shortName", expression = "java(mod.getNormalizedName())")
    PresetResponseModDto mapModToDto(WorkshopMod mod);

    List<PresetResponseModDto> mapModsToDtos(Collection<WorkshopMod> mods);

    void updateModPresetFromDto(@MappingTarget ModPreset modPreset, PresetResponseDto presetResponseDto);
}
