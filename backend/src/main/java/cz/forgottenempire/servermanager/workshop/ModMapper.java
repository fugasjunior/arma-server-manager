package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.api.model.CreatorDlcDto;
import cz.forgottenempire.servermanager.api.model.ModDto;
import cz.forgottenempire.servermanager.api.model.ServerType;
import cz.forgottenempire.servermanager.common.DateTimeMapper;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
interface ModMapper {

    @Mapping(target = "installationStatus", expression = "java(workshopMod.getInstallationStatus() != null ? workshopMod.getInstallationStatus().name() : null)")
    @Mapping(target = "errorStatus", expression = "java(workshopMod.getErrorStatus() != null ? workshopMod.getErrorStatus().name() : null)")
    ModDto modToModDto(WorkshopMod workshopMod);

    @Mapping(target = "biKeys", ignore = true)
    @Mapping(target = "serverType", ignore = true)
    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "installationStatus", ignore = true)
    @Mapping(target = "errorStatus", ignore = true)
    WorkshopMod modDtoToMod(ModDto workshopModDto);

    default CreatorDlcDto creatorDlcToCreatorDlcDto(Arma3CDLC cdlc) {
        return new CreatorDlcDto().id(cdlc.getId()).name(cdlc.getName());
    }

    List<ModDto> modsToModDtos(Collection<WorkshopMod> workshopMods);

    List<CreatorDlcDto> creatorDlcsToCreatorDlcDtos(Collection<Arma3CDLC> cdlcs);

    default ServerType mapServerType(cz.forgottenempire.servermanager.common.ServerType type) {
        return type != null ? ServerType.fromValue(type.name()) : null;
    }

}
