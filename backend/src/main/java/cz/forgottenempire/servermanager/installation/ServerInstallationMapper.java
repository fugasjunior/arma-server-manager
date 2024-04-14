package cz.forgottenempire.servermanager.installation;

import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
interface ServerInstallationMapper {

    @Mapping(source = "lastUpdatedAt", target = "lastUpdatedAt")
    ServerInstallationDto map(ServerInstallation serverInstallation);

    List<ServerInstallationDto> map(List<ServerInstallation> serverInstallations);
}
