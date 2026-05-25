package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.api.model.LocalModDto;
import cz.forgottenempire.servermanager.common.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
interface LocalModMapper {

    @Mapping(target = "uploadedAt", source = "uploadedAt")
    LocalModDto toDto(LocalMod mod);

    List<LocalModDto> toDtos(List<LocalMod> mods);
}
