package cz.forgottenempire.arma3servergui.installation;

import cz.forgottenempire.arma3servergui.common.InstallationStatus;
import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
class ServerInstallationDto {

    private ServerType type;
    private String version;
    private InstallationStatus installationStatus;
    private ErrorStatus errorStatus;
    private String lastUpdatedAt;
}
