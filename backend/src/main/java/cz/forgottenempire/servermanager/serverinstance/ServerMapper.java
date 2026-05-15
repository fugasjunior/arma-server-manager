package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.api.model.Arma3DifficultySettingsDto;
import cz.forgottenempire.servermanager.api.model.Arma3NetworkSettingsDto;
import cz.forgottenempire.servermanager.api.model.Arma3ServerDto;
import cz.forgottenempire.servermanager.api.model.CreatorDlcDto;
import cz.forgottenempire.servermanager.api.model.DayZServerDto;
import cz.forgottenempire.servermanager.api.model.LaunchParameterDto;
import cz.forgottenempire.servermanager.api.model.ReforgerModDto;
import cz.forgottenempire.servermanager.api.model.ReforgerServerDto;
import cz.forgottenempire.servermanager.api.model.ServerDto;
import cz.forgottenempire.servermanager.api.model.ServerInstanceInfoDto;
import cz.forgottenempire.servermanager.api.model.ServerWorkshopModDto;
import cz.forgottenempire.servermanager.common.DateTimeMapper;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3DifficultySettings;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3NetworkSettings;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.DayZServer;
import cz.forgottenempire.servermanager.serverinstance.entities.LaunchParameter;
import cz.forgottenempire.servermanager.serverinstance.entities.ReforgerMod;
import cz.forgottenempire.servermanager.serverinstance.entities.ReforgerServer;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
interface ServerMapper {

    @Mapping(source = "restartAutomatically", target = "automaticRestart.enabled")
    @Mapping(source = "automaticRestartTime", target = "automaticRestart.time")
    @Mapping(target = "type", constant = "ARMA3")
    Arma3ServerDto mapArma3ServerToDto(Arma3Server server);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    @Mapping(target = "additionalMods", ignore = true)
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    @Mapping(target = "modsAsParameters", ignore = true)
    Arma3Server mapArma3ServerDtoToEntity(Arma3ServerDto serverDto);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    @Mapping(target = "additionalMods", ignore = true)
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    @Mapping(target = "modsAsParameters", ignore = true)
    void updateArma3ServerFromDto(Arma3ServerDto serverDto, @MappingTarget Arma3Server server);

    @Mapping(source = "restartAutomatically", target = "automaticRestart.enabled")
    @Mapping(source = "automaticRestartTime", target = "automaticRestart.time")
    @Mapping(target = "type", constant = "DAYZ")
    @Mapping(target = "verifySignatures", constant = "false")
    DayZServerDto mapDayZServerToDto(DayZServer dayZServer);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    DayZServer mapDayZServerDtoToEntity(DayZServerDto serverDto);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    void updateDayZServerFromDto(DayZServerDto serverDto, @MappingTarget DayZServer server);

    @Mapping(source = "restartAutomatically", target = "automaticRestart.enabled")
    @Mapping(source = "automaticRestartTime", target = "automaticRestart.time")
    @Mapping(target = "type", constant = "REFORGER")
    ReforgerServerDto mapReforgerServerToDto(ReforgerServer reforgerServer);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    @Mapping(target = "launchParameters", ignore = true)
    @Mapping(target = "configFiles", ignore = true)
    ReforgerServer mapReforgerServerDtoToEntity(ReforgerServerDto serverDto);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
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
    Arma3NetworkSettings mapNetworkSettingsDtoToEntity(Arma3NetworkSettingsDto networkSettingsDto);

    default CreatorDlcDto mapCreatorDlcToDto(Arma3CDLC cdlc) {
        return new CreatorDlcDto().id(cdlc.getId()).name(cdlc.getName());
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
        ServerType type = server.getType();
        if (type == ServerType.ARMA3) {
            return mapArma3ServerToDto((Arma3Server) server);
        } else if (type == ServerType.DAYZ || type == ServerType.DAYZ_EXP) {
            return mapDayZServerToDto((DayZServer) server);
        } else if (type == ServerType.REFORGER) {
            return mapReforgerServerToDto((ReforgerServer) server);
        }
        throw new IllegalStateException("Unsupported server type");
    }

    default Server mapServerDtoToEntity(ServerDto serverDto) {
        String type = serverDto.getType();
        if ("ARMA3".equals(type)) {
            return mapArma3ServerDtoToEntity((Arma3ServerDto) serverDto);
        }
        if ("DAYZ".equals(type) || "DAYZ_EXP".equals(type)) {
            return mapDayZServerDtoToEntity((DayZServerDto) serverDto);
        }
        if ("REFORGER".equals(type)) {
            return mapReforgerServerDtoToEntity((ReforgerServerDto) serverDto);
        }
        throw new IllegalStateException("Unsupported server type");
    }

    default void updateServerFromDto(ServerDto serverDto, @MappingTarget Server server) {
        String type = serverDto.getType();
        if ("ARMA3".equals(type)) {
            updateArma3ServerFromDto((Arma3ServerDto) serverDto, (Arma3Server) server);
        } else if ("DAYZ".equals(type) || "DAYZ_EXP".equals(type)) {
            updateDayZServerFromDto((DayZServerDto) serverDto, (DayZServer) server);
        } else if ("REFORGER".equals(type)) {
            updateReforgerServerFromDto((ReforgerServerDto) serverDto, (ReforgerServer) server);
        } else {
            throw new IllegalStateException("Unsupported server type");
        }
    }

}
