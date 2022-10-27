package cz.forgottenempire.arma3servergui.server.additionalserver.mappers;

import cz.forgottenempire.arma3servergui.server.additionalserver.AdditionalServerInstanceInfo;
import cz.forgottenempire.arma3servergui.server.additionalserver.dtos.AdditionalServerDto;
import cz.forgottenempire.arma3servergui.server.additionalserver.entities.AdditionalServer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AdditionalServerMapper {

    @Mapping(source = "additionalServer.id", target = "id")
    @Mapping(source = "additionalServer.name", target = "name")
    @Mapping(source = "additionalServer.imageUrl", target = "imageUrl")
    @Mapping(source = "instanceInfo.alive", target = "alive")
    @Mapping(source = "instanceInfo.startedAt", target = "startedAt", dateFormat = "yyyy-MM-dd HH:mm")
    AdditionalServerDto from(AdditionalServer additionalServer, AdditionalServerInstanceInfo instanceInfo);

}
