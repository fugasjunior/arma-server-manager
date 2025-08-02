package cz.forgottenempire.servermanager.workshop;

import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface ModMapper {

    ModDto modToModDto(WorkshopMod workshopMod);

    @Mapping(target = "biKeys", ignore = true)
    WorkshopMod modDtoToMod(ModDto workshopModDto);

    CreatorDlcDto creatorDlcToCreatorDlcDto(Arma3CDLC cdlc);

    List<ModDto> modsToModDtos(Collection<WorkshopMod> workshopMods);

    List<CreatorDlcDto> creatorDlcsToCreatorDlcDtos(Collection<Arma3CDLC> cdlcs);
}
