package cz.forgottenempire.servermanager.additionalserver;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface AdditionalServerMapper {

    @Mapping(source = "additionalServer.id", target = "id")
    @Mapping(source = "additionalServer.name", target = "name")
    @Mapping(source = "additionalServer.imageUrl", target = "imageUrl")
    @Mapping(source = "instanceInfo.alive", target = "alive")
    @Mapping(source = "instanceInfo.startedAt", target = "startedAt")
    AdditionalServerDto from(AdditionalServer additionalServer, AdditionalServerInstanceInfo instanceInfo);

}
