package cz.forgottenempire.arma3servergui.server.installation.dtos;

import cz.forgottenempire.arma3servergui.server.ServerType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerInstallationDto {
    private ServerType type;
    private String version;
    private boolean isInstalled;
    private boolean isUpdating;
}
