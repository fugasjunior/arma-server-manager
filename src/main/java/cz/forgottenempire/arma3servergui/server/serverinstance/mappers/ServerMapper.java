package cz.forgottenempire.arma3servergui.server.serverinstance.mappers;

import cz.forgottenempire.arma3servergui.server.ServerInstanceInfo;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.ServerInstanceInfoDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.ServerWorkshopModDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.entities.Server;
import cz.forgottenempire.arma3servergui.workshop.Arma3CDLC;
import cz.forgottenempire.arma3servergui.workshop.dtos.CreatorDlcDto;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.Collection;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServerMapper {

    ServerDto serverToServerDto(Server server);

    Server serverDtoToServer(ServerDto serverDto);

    void updateServerFromDto(ServerDto serverDto, @MappingTarget Server server);

    @Mapping(source = "startedAt", target = "startedAt", dateFormat = "yyyy-MM-dd HH:mm")
    ServerInstanceInfoDto serverInstanceInfoToServerInstanceInfoDto(ServerInstanceInfo instanceInfo);

    ServerWorkshopModDto workshopModToServerWorkshopModDto(WorkshopMod workshopMod);

    WorkshopMod ServerWorkshopModDtoToWorkshopMod(ServerWorkshopModDto serverWorkshopModDto);

    List<ServerDto> serversToServerDtos(Collection<Server> servers);

    default CreatorDlcDto creatorDlcToCreatorDlcDto(Arma3CDLC cdlc) {
        return new CreatorDlcDto(cdlc.getId(), cdlc.getName());
    }

    default Arma3CDLC creatorDlcDtoToCreatorDlc(CreatorDlcDto creatorDlcDto) {
        return Arma3CDLC.fromId(creatorDlcDto.getId());
    }
}
