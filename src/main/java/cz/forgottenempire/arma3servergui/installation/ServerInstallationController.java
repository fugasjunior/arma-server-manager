package cz.forgottenempire.arma3servergui.installation;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.common.exceptions.ServerUnsupportedOnOsException;
import cz.forgottenempire.arma3servergui.util.SystemUtils;
import cz.forgottenempire.arma3servergui.util.SystemUtils.OSType;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/server/installation")
@Slf4j
class ServerInstallationController {

    private final ServerInstallationService installationService;
    private final ServerInstallerService installerService;
    private final ServerInstallationMapper mapper = Mappers.getMapper(ServerInstallationMapper.class);

    @Autowired
    public ServerInstallationController(
            ServerInstallationService installationService,
            ServerInstallerService installerService
    ) {
        this.installationService = installationService;
        this.installerService = installerService;
    }

    @GetMapping
    public ResponseEntity<ServerInstallationsDto> getAllInstalations() {
        List<ServerInstallation> installations = installationService.getAllServerInstallations().stream()
                .filter(i -> SystemUtils.getOsType() != OSType.LINUX || i.getType() != ServerType.DAYZ)
                .toList();
        return ResponseEntity.ok(new ServerInstallationsDto(mapper.map(installations)));
    }

    @GetMapping("/{type}")
    public ResponseEntity<ServerInstallationDto> getInstallation(@PathVariable ServerType type) {
        checkServerSupportedOnOS(type);
        ServerInstallation installation = installationService.getServerInstallation(type);
        return ResponseEntity.ok(mapper.map(installation));
    }

    @PostMapping("/{type}")
    public ResponseEntity<ServerInstallationDto> installOrUpdateServer(@PathVariable ServerType type) {
        checkServerSupportedOnOS(type);
        installerService.installServer(type);
        ServerInstallation installation = installationService.getServerInstallation(type);
        return ResponseEntity.ok(mapper.map(installation));
    }

    private void checkServerSupportedOnOS(ServerType type) {
        if (type == ServerType.DAYZ && SystemUtils.getOsType() == OSType.LINUX) {
            throw new ServerUnsupportedOnOsException(
                    "DayZ server is not supported on Linux yet. Use DayZ Experimental server instead");
        }
    }
}
