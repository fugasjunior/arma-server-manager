package cz.forgottenempire.arma3servergui.serverinstance;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.serverinstance.dtos.Arma3ServerDto;
import cz.forgottenempire.arma3servergui.serverinstance.dtos.DayZServerDto;
import cz.forgottenempire.arma3servergui.serverinstance.dtos.ReforgerServerDto;
import cz.forgottenempire.arma3servergui.serverinstance.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.serverinstance.dtos.ServerInstanceInfoDto;
import cz.forgottenempire.arma3servergui.serverinstance.dtos.ServerWorkshopModDto;
import cz.forgottenempire.arma3servergui.serverinstance.entities.Arma3Server;
import cz.forgottenempire.arma3servergui.serverinstance.entities.DayZServer;
import cz.forgottenempire.arma3servergui.serverinstance.entities.ReforgerServer;
import cz.forgottenempire.arma3servergui.serverinstance.entities.Server;
import cz.forgottenempire.arma3servergui.workshop.Arma3CDLC;
import cz.forgottenempire.arma3servergui.workshop.CreatorDlcDto;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
interface ServerMapper {

    Arma3ServerDto mapArma3ServerToDto(Arma3Server server);

    Arma3Server mapArma3ServerDtoToEntity(Arma3ServerDto serverDto);

    void updateArma3ServerFromDto(Arma3ServerDto serverDto, @MappingTarget Arma3Server server);

    DayZServerDto mapDayZServerToDto(DayZServer dayZServer);

    DayZServer mapDayZServerDtoToEntity(DayZServerDto serverDto);

    void updateDayZServerFromDto(DayZServerDto serverDto, @MappingTarget DayZServer server);

    ReforgerServerDto mapReforgerServerToDto(ReforgerServer reforgerServer);

    ReforgerServer mapReforgerServerDtoToEntity(ReforgerServerDto serverDto);

    void updateReforgerServerFromDto(ReforgerServerDto serverDto, @MappingTarget ReforgerServer server);

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
        ServerType type = server.getType();
        if (type == ServerType.ARMA3) {
            return mapArma3ServerToDto((Arma3Server) server);
        }
        if (type == ServerType.DAYZ || type == ServerType.DAYZ_EXP) {
            return mapDayZServerToDto((DayZServer) server);
        }
        if (type == ServerType.REFORGER) {
            return mapReforgerServerToDto((ReforgerServer) server);
        }

        throw new IllegalStateException("Unsupported server type");
    }

    default Server mapServerDtoToEntity(ServerDto serverDto) {
        ServerType type = serverDto.getType();
        if (type == ServerType.ARMA3) {
            return mapArma3ServerDtoToEntity((Arma3ServerDto) serverDto);
        }
        if (type == ServerType.DAYZ || type == ServerType.DAYZ_EXP) {
            return mapDayZServerDtoToEntity((DayZServerDto) serverDto);
        }
        if (type == ServerType.REFORGER) {
            return mapReforgerServerDtoToEntity((ReforgerServerDto) serverDto);
        }

        throw new IllegalStateException("Unsupported server type");
    }

    default void updateServerFromDto(ServerDto serverDto, @MappingTarget Server server) {
        ServerType type = serverDto.getType();
        if (type == ServerType.ARMA3) {
            updateArma3ServerFromDto((Arma3ServerDto) serverDto, (Arma3Server) server);
        } else if (type == ServerType.DAYZ || type == ServerType.DAYZ_EXP) {
            updateDayZServerFromDto((DayZServerDto) serverDto, (DayZServer) server);
        } else if (type == ServerType.REFORGER) {
            updateReforgerServerFromDto((ReforgerServerDto) serverDto, (ReforgerServer) server);
        } else {
            throw new IllegalStateException("Unsupported server type");
        }
    }

}
