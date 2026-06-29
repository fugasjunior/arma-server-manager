package cz.forgottenempire.servermanager.additionalserver;

import cz.forgottenempire.servermanager.api.model.AdditionalServerDto;
import cz.forgottenempire.servermanager.common.DateTimeMapper;
import cz.forgottenempire.servermanager.common.ServerStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
interface AdditionalServerMapper {

    @Mapping(source = "additionalServer.id", target = "id")
    @Mapping(source = "additionalServer.name", target = "name")
    @Mapping(source = "additionalServer.imageUrl", target = "imageUrl")
    @Mapping(source = "additionalServer.autoStart", target = "autoStart")
    @Mapping(source = "instanceInfo.alive", target = "alive")
    @Mapping(source = "instanceInfo.startedAt", target = "startedAt")
    @Mapping(source = "instanceInfo.status", target = "status")
    AdditionalServerDto from(AdditionalServer additionalServer, AdditionalServerInstanceInfo instanceInfo);

    default cz.forgottenempire.servermanager.api.model.ServerStatus mapServerStatus(ServerStatus status) {
        if (status == null) return null;
        return cz.forgottenempire.servermanager.api.model.ServerStatus.valueOf(status.name());
    }
}
