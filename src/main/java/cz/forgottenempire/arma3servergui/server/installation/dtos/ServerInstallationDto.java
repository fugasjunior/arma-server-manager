package cz.forgottenempire.arma3servergui.server.installation.dtos;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod.InstallationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServerInstallationDto {
    private ServerType type;
    private String version;
    private InstallationStatus installationStatus;
    private ErrorStatus errorStatus;
}
