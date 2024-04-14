package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.modpreset.dtos.PresetResponseDto;
import cz.forgottenempire.servermanager.modpreset.dtos.PresetResponseModDto;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
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
