package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.api.model.PresetResponseDto;
import cz.forgottenempire.servermanager.api.model.PresetResponseModDto;
import cz.forgottenempire.servermanager.api.model.ServerType;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
interface ModPresetMapper {

    PresetResponseDto mapToModPresetDto(ModPreset modPreset);

    @AfterMapping
    default void computeTotalModsSize(ModPreset source, @MappingTarget PresetResponseDto target) {
        long total = source.getMods().stream()
                .map(WorkshopMod::getFileSize)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .sum();
        target.setTotalModsSize(total > 0 ? total : null);
    }

    List<PresetResponseDto> mapToModPresetDtos(Collection<ModPreset> modPresets);

    @Mapping(target = "shortName", expression = "java(mod.getNormalizedName())")
    PresetResponseModDto mapModToDto(WorkshopMod mod);

    List<PresetResponseModDto> mapModsToDtos(Collection<WorkshopMod> mods);

    void updateModPresetFromDto(@MappingTarget ModPreset modPreset, PresetResponseDto presetResponseDto);

    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "loadOnClient", ignore = true)
    @Mapping(target = "loadOnServer", ignore = true)
    @Mapping(target = "loadOnHeadlessClient", ignore = true)
    @Mapping(target = "installationStatus", ignore = true)
    @Mapping(target = "errorStatus", ignore = true)
    @Mapping(target = "serverType", ignore = true)
    @Mapping(target = "biKeys", ignore = true)
    WorkshopMod presetResponseModDtoToWorkshopMod(PresetResponseModDto modDto);

    default ServerType mapServerType(cz.forgottenempire.servermanager.common.ServerType type) {
        return type != null ? ServerType.fromValue(type.name()) : null;
    }

    default cz.forgottenempire.servermanager.common.ServerType mapServerType(ServerType type) {
        return type != null ? cz.forgottenempire.servermanager.common.ServerType.valueOf(type.name()) : null;
    }
}
