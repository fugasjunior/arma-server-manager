package cz.forgottenempire.servermanager.additionalserver;

import cz.forgottenempire.servermanager.api.model.AdditionalServerDto;
import cz.forgottenempire.servermanager.common.DateTimeMapper;
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
    AdditionalServerDto from(AdditionalServer additionalServer, AdditionalServerInstanceInfo instanceInfo);
}
