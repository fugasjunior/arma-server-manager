package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.api.model.Arma3DifficultySettingsDto;
import cz.forgottenempire.servermanager.api.model.Arma3NetworkSettingsDto;
import cz.forgottenempire.servermanager.api.model.Arma3ServerDto;
import cz.forgottenempire.servermanager.api.model.ConfigOverrideDto;
import cz.forgottenempire.servermanager.api.model.CreatorDlcDto;
import cz.forgottenempire.servermanager.api.model.DayZServerDto;
import cz.forgottenempire.servermanager.api.model.LaunchParameterDto;
import cz.forgottenempire.servermanager.api.model.ReforgerModDto;
import cz.forgottenempire.servermanager.api.model.ReforgerServerDto;
import cz.forgottenempire.servermanager.api.model.ServerDto;
import cz.forgottenempire.servermanager.api.model.ServerInstanceInfoDto;
import cz.forgottenempire.servermanager.api.model.ServerLocalModDto;
import cz.forgottenempire.servermanager.api.model.ServerWorkshopModDto;
import cz.forgottenempire.servermanager.common.DateTimeMapper;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.localmod.LocalMod;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3DifficultySettings;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3NetworkSettings;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3ServerActiveLocalMod;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3ServerActiveMod;
import cz.forgottenempire.servermanager.serverinstance.entities.DayZServer;
import cz.forgottenempire.servermanager.serverinstance.entities.DayZServerActiveLocalMod;
import cz.forgottenempire.servermanager.serverinstance.entities.DayZServerActiveMod;
import cz.forgottenempire.servermanager.serverinstance.entities.LaunchParameter;
import cz.forgottenempire.servermanager.serverinstance.entities.ReforgerMod;
import cz.forgottenempire.servermanager.serverinstance.entities.ReforgerServer;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.entities.ServerConfigOverride;
import cz.forgottenempire.servermanager.workshop.Arma3CDLC;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import java.util.List;
import org.mapstruct.AfterMapping;
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
    @Mapping(target = "activeMods", ignore = true)
    @Mapping(target = "activeLocalMods", ignore = true)
    Arma3Server mapArma3ServerDtoToEntity(Arma3ServerDto serverDto);

    @AfterMapping
    default void syncArma3ModsFromDto(Arma3ServerDto dto, @MappingTarget Arma3Server server) {
        if (dto.getActiveMods() != null) {
            server.getActiveMods().addAll(dto.getActiveMods().stream().map(this::workshopDtoToArma3ActiveMod).toList());
        }
        if (dto.getActiveLocalMods() != null) {
            server.getActiveLocalMods().addAll(dto.getActiveLocalMods().stream().map(this::localDtoToArma3LocalActiveMod).toList());
        }
    }

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    @Mapping(target = "activeMods", ignore = true)
    @Mapping(target = "activeLocalMods", ignore = true)
    void updateArma3ServerFromDto(Arma3ServerDto serverDto, @MappingTarget Arma3Server server);

    @Mapping(source = "restartAutomatically", target = "automaticRestart.enabled")
    @Mapping(source = "automaticRestartTime", target = "automaticRestart.time")
    @Mapping(target = "type", constant = "DAYZ")
    @Mapping(target = "verifySignatures", constant = "false")
    DayZServerDto mapDayZServerToDto(DayZServer dayZServer);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    @Mapping(target = "activeMods", ignore = true)
    @Mapping(target = "activeLocalMods", ignore = true)
    DayZServer mapDayZServerDtoToEntity(DayZServerDto serverDto);

    @AfterMapping
    default void syncDayZModsFromDto(DayZServerDto dto, @MappingTarget DayZServer server) {
        if (dto.getActiveMods() != null) {
            server.getActiveMods().addAll(dto.getActiveMods().stream().map(this::workshopDtoToDayZActiveMod).toList());
        }
        if (dto.getActiveLocalMods() != null) {
            server.getActiveLocalMods().addAll(dto.getActiveLocalMods().stream().map(this::localDtoToDayZLocalActiveMod).toList());
        }
    }

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    @Mapping(target = "activeMods", ignore = true)
    @Mapping(target = "activeLocalMods", ignore = true)
    void updateDayZServerFromDto(DayZServerDto serverDto, @MappingTarget DayZServer server);

    @Mapping(source = "restartAutomatically", target = "automaticRestart.enabled")
    @Mapping(source = "automaticRestartTime", target = "automaticRestart.time")
    @Mapping(target = "type", constant = "REFORGER")
    ReforgerServerDto mapReforgerServerToDto(ReforgerServer reforgerServer);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    ReforgerServer mapReforgerServerDtoToEntity(ReforgerServerDto serverDto);

    @Mapping(source = "automaticRestart.enabled", target = "restartAutomatically")
    @Mapping(source = "automaticRestart.time", target = "automaticRestartTime")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", expression = "java(cz.forgottenempire.servermanager.common.ServerType.valueOf(serverDto.getType()))")
    void updateReforgerServerFromDto(ReforgerServerDto serverDto, @MappingTarget ReforgerServer server);

    @Mapping(source = "startedAt", target = "startedAt")
    ServerInstanceInfoDto mapServerInstanceInfoToDto(ServerInstanceInfo instanceInfo);

    @Mapping(target = "position", ignore = true)
    ServerWorkshopModDto mapWorkshopModToDto(WorkshopMod workshopMod);

    @Mapping(target = "position", ignore = true)
    ServerLocalModDto mapLocalModToDto(LocalMod localMod);

    @Mapping(target = "lastUpdated", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "loadOnClient", ignore = true)
    @Mapping(target = "loadOnServer", ignore = true)
    @Mapping(target = "loadOnHeadlessClient", ignore = true)
    @Mapping(target = "installationStatus", ignore = true)
    @Mapping(target = "errorStatus", ignore = true)
    @Mapping(target = "serverType", ignore = true)
    @Mapping(target = "biKeys", ignore = true)
    WorkshopMod mapSteamWorkshopModDtoToEntity(ServerWorkshopModDto serverWorkshopModDto);

    @Mapping(target = "serverType", ignore = true)
    @Mapping(target = "fileSize", ignore = true)
    @Mapping(target = "loadOnClient", ignore = true)
    @Mapping(target = "loadOnServer", ignore = true)
    @Mapping(target = "loadOnHeadlessClient", ignore = true)
    @Mapping(target = "uploadedAt", ignore = true)
    @Mapping(target = "biKeys", ignore = true)
    LocalMod mapLocalModDtoToEntity(ServerLocalModDto serverLocalModDto);

    @Mapping(source = "mod.id", target = "id")
    @Mapping(source = "mod.name", target = "name")
    @Mapping(source = "position", target = "position")
    ServerWorkshopModDto arma3ActiveModToDto(Arma3ServerActiveMod entry);

    @Mapping(source = "mod.id", target = "id")
    @Mapping(source = "mod.name", target = "name")
    @Mapping(source = "position", target = "position")
    ServerLocalModDto arma3LocalActiveModToDto(Arma3ServerActiveLocalMod entry);

    @Mapping(source = "mod.id", target = "id")
    @Mapping(source = "mod.name", target = "name")
    @Mapping(source = "position", target = "position")
    ServerWorkshopModDto dayZActiveModToDto(DayZServerActiveMod entry);

    @Mapping(source = "mod.id", target = "id")
    @Mapping(source = "mod.name", target = "name")
    @Mapping(source = "position", target = "position")
    ServerLocalModDto dayZLocalActiveModToDto(DayZServerActiveLocalMod entry);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "mod.id")
    @Mapping(source = "position", target = "position")
    @Mapping(target = "mod.name", ignore = true)
    @Mapping(target = "mod.loadOnClient", ignore = true)
    @Mapping(target = "mod.loadOnServer", ignore = true)
    @Mapping(target = "mod.loadOnHeadlessClient", ignore = true)
    @Mapping(target = "mod.lastUpdated", ignore = true)
    @Mapping(target = "mod.fileSize", ignore = true)
    @Mapping(target = "mod.installationStatus", ignore = true)
    @Mapping(target = "mod.errorStatus", ignore = true)
    @Mapping(target = "mod.serverType", ignore = true)
    @Mapping(target = "mod.biKeys", ignore = true)
    Arma3ServerActiveMod workshopDtoToArma3ActiveMod(ServerWorkshopModDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "mod.id")
    @Mapping(source = "position", target = "position")
    @Mapping(target = "mod.name", ignore = true)
    @Mapping(target = "mod.serverType", ignore = true)
    @Mapping(target = "mod.fileSize", ignore = true)
    @Mapping(target = "mod.loadOnClient", ignore = true)
    @Mapping(target = "mod.loadOnServer", ignore = true)
    @Mapping(target = "mod.loadOnHeadlessClient", ignore = true)
    @Mapping(target = "mod.uploadedAt", ignore = true)
    @Mapping(target = "mod.biKeys", ignore = true)
    Arma3ServerActiveLocalMod localDtoToArma3LocalActiveMod(ServerLocalModDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "mod.id")
    @Mapping(source = "position", target = "position")
    @Mapping(target = "mod.name", ignore = true)
    @Mapping(target = "mod.loadOnClient", ignore = true)
    @Mapping(target = "mod.loadOnServer", ignore = true)
    @Mapping(target = "mod.loadOnHeadlessClient", ignore = true)
    @Mapping(target = "mod.lastUpdated", ignore = true)
    @Mapping(target = "mod.fileSize", ignore = true)
    @Mapping(target = "mod.installationStatus", ignore = true)
    @Mapping(target = "mod.errorStatus", ignore = true)
    @Mapping(target = "mod.serverType", ignore = true)
    @Mapping(target = "mod.biKeys", ignore = true)
    DayZServerActiveMod workshopDtoToDayZActiveMod(ServerWorkshopModDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "mod.id")
    @Mapping(source = "position", target = "position")
    @Mapping(target = "mod.name", ignore = true)
    @Mapping(target = "mod.serverType", ignore = true)
    @Mapping(target = "mod.fileSize", ignore = true)
    @Mapping(target = "mod.loadOnClient", ignore = true)
    @Mapping(target = "mod.loadOnServer", ignore = true)
    @Mapping(target = "mod.loadOnHeadlessClient", ignore = true)
    @Mapping(target = "mod.uploadedAt", ignore = true)
    @Mapping(target = "mod.biKeys", ignore = true)
    DayZServerActiveLocalMod localDtoToDayZLocalActiveMod(ServerLocalModDto dto);


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

    default ConfigOverrideDto mapOverrideToDto(ServerConfigOverride override) {
        if (override == null || override.getConfigKey() == null) return null;
        return new ConfigOverrideDto()
                .configKey(override.getConfigKey().name())
                .advanced(true)
                .content(override.getContent());
    }

    default List<ConfigOverrideDto> mapOverridesToDtos(List<ServerConfigOverride> overrides) {
        if (overrides == null) return List.of();
        return overrides.stream()
                .filter(o -> o.getConfigKey() != null)
                .map(this::mapOverrideToDto)
                .toList();
    }

}
