package cz.forgottenempire.arma3servergui.installation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ServerInstallationsDto {

    private List<ServerInstallationDto> serverInstallations;
}
