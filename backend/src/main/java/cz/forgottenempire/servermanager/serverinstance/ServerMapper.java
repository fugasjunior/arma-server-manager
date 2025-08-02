package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.dtos.*;
import cz.forgottenempire.servermanager.serverinstance.entities.*;
import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import cz.forgottenempire.servermanager.workshop.CreatorDlcDto;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
interface ServerMapper {

    @Mapping(source = "restartAutomatically", target = "automaticRestart.enabled")
    @Mapping(source = "automaticRestartTime", target = "automaticRestart.time")
    Arma3ServerDto mapArma3ServerToDto(Arma3Server server);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "additionalMods", ignore = true)
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    @Mapping(target = "modsAsParameters", ignore = true)
    Arma3Server mapArma3ServerDtoToEntity(Arma3ServerDto serverDto);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "additionalMods", ignore = true)
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    @Mapping(target = "modsAsParameters", ignore = true)
    void updateArma3ServerFromDto(Arma3ServerDto serverDto, @MappingTarget Arma3Server server);

    @Mapping(source = "restartAutomatically", target = "automaticRestart.enabled")
    @Mapping(source = "automaticRestartTime", target = "automaticRestart.time")
    @Mapping(target = "verifySignatures", constant = "false")
    DayZServerDto mapDayZServerToDto(DayZServer dayZServer);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    DayZServer mapDayZServerDtoToEntity(DayZServerDto serverDto);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    void updateDayZServerFromDto(DayZServerDto serverDto, @MappingTarget DayZServer server);

    @Mapping(source = "restartAutomatically", target = "automaticRestart.enabled")
    @Mapping(source = "automaticRestartTime", target = "automaticRestart.time")
    ReforgerServerDto mapReforgerServerToDto(ReforgerServer reforgerServer);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    ReforgerServer mapReforgerServerDtoToEntity(ReforgerServerDto serverDto);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    void updateReforgerServerFromDto(ReforgerServerDto serverDto, @MappingTarget ReforgerServer server);

    @Mapping(source = "startedAt", target = "startedAt")
    ServerInstanceInfoDto mapServerInstanceInfoToDto(ServerInstanceInfo instanceInfo);

    ServerWorkshopModDto mapWorkshopModToDto(WorkshopMod workshopMod);

    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "serverOnly", ignore = true)
    @Mapping(target = "installationStatus", ignore = true)
    @Mapping(target = "errorStatus", ignore = true)
    @Mapping(target = "serverType", ignore = true)
    @Mapping(target = "biKeys", ignore = true)
    WorkshopMod mapSteamWorkshopModDtoToEntity(ServerWorkshopModDto serverWorkshopModDto);

    Arma3DifficultySettingsDto mapDifficultySettingsToDto(Arma3DifficultySettings settings);

    @Mapping(target = "id", ignore = true)
    Arma3DifficultySettings mapDifficultySettingsDtoToEntity(Arma3DifficultySettingsDto difficultySettingsDto);

    Arma3NetworkSettingsDto mapNetworkSettingsToDto(Arma3NetworkSettings settings);

    @Mapping(target = "id", ignore = true)
    Arma3NetworkSettings mapNetworkSettingsDtoToEntity(Arma3NetworkSettingsDto difficultySettingsDto);

    default CreatorDlcDto mapCreatorDlcToDto(Arma3CDLC cdlc) {
        return new CreatorDlcDto(cdlc.getId(), cdlc.getName());
    }

    default Arma3CDLC mapCreatorDlcDtoToEntity(CreatorDlcDto creatorDlcDto) {
        return Arma3CDLC.fromId(creatorDlcDto.getId());
    }

    ReforgerModDto mapReforgerModToDto(ReforgerMod reforgerMod);

    ReforgerMod mapReforgerModDtoToEntity(ReforgerModDto reforgerModDto);

    LaunchParameterDto mapCustomLaunchParameterToDto(LaunchParameter launchParameter);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "server", ignore = true)
    LaunchParameter mapCustomLaunchParameterDtoToEntity(LaunchParameterDto launchParameterDto);

    default ServerDto mapServerToDto(Server server) {
        ServerDto serverDto;
        ServerType type = server.getType();
        if (type == ServerType.ARMA3) {
            serverDto = mapArma3ServerToDto((Arma3Server) server);
        } else if (type == ServerType.DAYZ || type == ServerType.DAYZ_EXP) {
            serverDto = mapDayZServerToDto((DayZServer) server);
        } else if (type == ServerType.REFORGER) {
            serverDto = mapReforgerServerToDto((ReforgerServer) server);
        } else {
            throw new IllegalStateException("Unsupported server type");
        }

        return serverDto;
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
