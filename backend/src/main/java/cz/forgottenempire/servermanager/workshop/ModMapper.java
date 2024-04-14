package cz.forgottenempire.servermanager.workshop;

import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
interface ModMapper {

    ModDto modToModDto(WorkshopMod workshopMod);

    WorkshopMod modDtoToMod(ModDto workshopModDto);

    CreatorDlcDto creatorDlcToCreatorDlcDto(Arma3CDLC cdlc);

    List<ModDto> modsToModDtos(Collection<WorkshopMod> workshopMods);

    List<CreatorDlcDto> creatorDlcsToCreatorDlcDtos(Collection<Arma3CDLC> cdlcs);
}
