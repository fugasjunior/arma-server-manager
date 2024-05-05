package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
class ServerInstallationDto {

    private ServerType type;
    private String version;
    private InstallationStatus installationStatus;
    private ErrorStatus errorStatus;
    private String lastUpdatedAt;
    private ServerInstallation.Branch branch;
    private Set<ServerInstallation.Branch> availableBranches;
}
