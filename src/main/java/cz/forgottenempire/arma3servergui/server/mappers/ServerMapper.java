package cz.forgottenempire.arma3servergui.server.mappers;

import cz.forgottenempire.arma3servergui.creatorDLC.entities.CreatorDLC;
import cz.forgottenempire.arma3servergui.server.dtos.ServerCreatorDLCDto;
import cz.forgottenempire.arma3servergui.server.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.server.dtos.ServerWorkshopModDto;
import cz.forgottenempire.arma3servergui.server.dtos.ServersDto;
import cz.forgottenempire.arma3servergui.server.entities.Server;
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

    ServerWorkshopModDto workshopModToServerWorkshopModDto(WorkshopMod workshopMod);

    WorkshopMod ServerWorkshopModDtoToWorkshopMod(ServerWorkshopModDto serverWorkshopModDto);

    ServerCreatorDLCDto creatorDLCToServerCreatorDLCDto(CreatorDLC creatorDLC);

    CreatorDLC ServerCreatorDLCDtoToCreatorDLC(ServerCreatorDLCDto serverCreatorDLCDto);

    List<ServerDto> serversToServerDtos(Collection<Server> servers);
}
