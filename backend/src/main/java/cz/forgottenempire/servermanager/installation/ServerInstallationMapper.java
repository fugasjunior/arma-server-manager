package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.api.model.InstallationBranch;
import cz.forgottenempire.servermanager.api.model.InstallationStatus;
import cz.forgottenempire.servermanager.api.model.ErrorStatus;
import cz.forgottenempire.servermanager.api.model.ServerInstallationDto;
import cz.forgottenempire.servermanager.api.model.ServerType;
import cz.forgottenempire.servermanager.common.DateTimeMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
interface ServerInstallationMapper {

    @Mapping(source = "lastUpdatedAt", target = "lastUpdatedAt")
    ServerInstallationDto map(ServerInstallation serverInstallation);

    List<ServerInstallationDto> map(List<ServerInstallation> serverInstallations);

    default ServerType mapServerType(cz.forgottenempire.servermanager.common.ServerType type) {
        return type != null ? ServerType.fromValue(type.name()) : null;
    }

    default InstallationStatus mapInstallationStatus(cz.forgottenempire.servermanager.common.InstallationStatus status) {
        return status != null ? InstallationStatus.fromValue(status.name()) : null;
    }

    default ErrorStatus mapErrorStatus(cz.forgottenempire.servermanager.steamcmd.ErrorStatus status) {
        return status != null ? ErrorStatus.fromValue(status.name()) : null;
    }

    default InstallationBranch mapBranch(ServerInstallation.Branch branch) {
        return branch != null ? InstallationBranch.fromValue(branch.name()) : null;
    }
}
