package cz.forgottenempire.arma3servergui.workshop.mappers;

import cz.forgottenempire.arma3servergui.workshop.Arma3CDLC;
import cz.forgottenempire.arma3servergui.workshop.dtos.CreatorDlcDto;
import cz.forgottenempire.arma3servergui.workshop.dtos.ModDto;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ModMapper {

    ModDto modToModDto(WorkshopMod workshopMod);

    WorkshopMod modDtoToMod(ModDto workshopModDto);

    CreatorDlcDto creatorDlcToCreatorDlcDto(Arma3CDLC cdlc);

    List<ModDto> modsToModDtos(Collection<WorkshopMod> workshopMods);

    List<CreatorDlcDto> creatorDlcsToCreatorDlcDtos(Collection<Arma3CDLC> cdlcs);
}
