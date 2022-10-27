package cz.forgottenempire.arma3servergui.server.installation;

import cz.forgottenempire.arma3servergui.server.installation.dtos.ServerInstallationDto;
import cz.forgottenempire.arma3servergui.server.installation.entities.ServerInstallation;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServerInstallationMapper {

    @Mapping(source = "lastUpdatedAt", target = "lastUpdatedAt", dateFormat = "yyyy-MM-dd HH:mm")
    ServerInstallationDto map(ServerInstallation serverInstallation);

    List<ServerInstallationDto> map(List<ServerInstallation> serverInstallations);
}
