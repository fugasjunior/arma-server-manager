package cz.forgottenempire.arma3servergui.server.serverinstance.mappers;

import cz.forgottenempire.arma3servergui.server.ServerInstanceInfo;
import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.Arma3ServerDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.DayZServerDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.ServerInstanceInfoDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.ServerWorkshopModDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.entities.Arma3Server;
import cz.forgottenempire.arma3servergui.server.serverinstance.entities.DayZServer;
import cz.forgottenempire.arma3servergui.server.serverinstance.entities.Server;
import cz.forgottenempire.arma3servergui.workshop.Arma3CDLC;
import cz.forgottenempire.arma3servergui.workshop.dtos.CreatorDlcDto;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ServerMapper {

    Arma3ServerDto mapArma3ServerToDto(Arma3Server server);

    Arma3Server mapArma3ServerDtoToEntity(Arma3ServerDto serverDto);

    void updateArma3ServerFromDto(Arma3ServerDto serverDto, @MappingTarget Arma3Server server);

    DayZServerDto mapDayZServerToDto(DayZServer dayZServer);

    DayZServer mapDayZServerDtoToEntity(DayZServerDto serverDto);

    void updateDayZServerFromDto(DayZServerDto serverDto, @MappingTarget DayZServer server);

    @Mapping(source = "startedAt", target = "startedAt", dateFormat = "yyyy-MM-dd HH:mm")
    ServerInstanceInfoDto mapServerInstanceInfoToDto(ServerInstanceInfo instanceInfo);

    ServerWorkshopModDto mapWorkshopModToDto(WorkshopMod workshopMod);

    WorkshopMod mapSteamWorkshopModDtoToEntity(ServerWorkshopModDto serverWorkshopModDto);

    default CreatorDlcDto mapCreatorDlcToDto(Arma3CDLC cdlc) {
        return new CreatorDlcDto(cdlc.getId(), cdlc.getName());
    }

    default Arma3CDLC mapCreatorDlcDtoToEntity(CreatorDlcDto creatorDlcDto) {
        return Arma3CDLC.fromId(creatorDlcDto.getId());
    }

    default ServerDto mapServerToDto(Server server) {
        if (server.getType() == ServerType.ARMA3) {
            return mapArma3ServerToDto((Arma3Server) server);
        }
        if (server.getType() == ServerType.DAYZ || server.getType() == ServerType.DAYZ_EXP) {
            return mapDayZServerToDto((DayZServer) server);
        }
        throw new IllegalStateException("Unsupported server type");
    }

    default Server mapServerDtoToEntity(ServerDto serverDto) {
        if (serverDto.getType() == ServerType.ARMA3) {
            return mapArma3ServerDtoToEntity((Arma3ServerDto) serverDto);
        }
        if (serverDto.getType() == ServerType.DAYZ || serverDto.getType() == ServerType.DAYZ_EXP) {
            return mapDayZServerDtoToEntity((DayZServerDto) serverDto);
        }
        throw new IllegalStateException("Unsupported server type");
    }

    default void updateServerFromDto(ServerDto serverDto, @MappingTarget Server server) {
        if (serverDto.getType() == ServerType.ARMA3) {
            updateArma3ServerFromDto((Arma3ServerDto) serverDto, (Arma3Server) server);
        } else if (serverDto.getType() == ServerType.DAYZ || serverDto.getType() == ServerType.DAYZ_EXP) {
            updateDayZServerFromDto((DayZServerDto) serverDto, (DayZServer) server);
        } else {
            throw new IllegalStateException("Unsupported server type");
        }
    }

}
