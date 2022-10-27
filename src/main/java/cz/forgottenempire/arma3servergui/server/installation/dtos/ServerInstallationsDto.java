package cz.forgottenempire.arma3servergui.server.installation.dtos;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class ServerInstallationsDto {
    private List<ServerInstallationDto> serverInstallations;
}
