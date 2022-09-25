package cz.forgottenempire.arma3servergui.workshop.mappers;

import cz.forgottenempire.arma3servergui.workshop.dtos.WorkshopModDto;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WorkshopModMapper {

    WorkshopModDto modToModDto(WorkshopMod workshopMod);

    WorkshopMod modDtoToMod(WorkshopModDto workshopModDto);

    List<WorkshopModDto> modsToModDtos(Collection<WorkshopMod> workshopMods);
}
