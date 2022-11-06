package cz.forgottenempire.servermanager.installation;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class ServerInstallationsDto {

    private List<ServerInstallationDto> serverInstallations;
}
