package cz.forgottenempire.arma3servergui.server.installation.controllers;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.server.installation.ServerInstallationMapper;
import cz.forgottenempire.arma3servergui.server.installation.dtos.ServerInstallationsDto;
import cz.forgottenempire.arma3servergui.server.installation.entities.ServerInstallation;
import cz.forgottenempire.arma3servergui.server.installation.services.ServerInstallationService;
import cz.forgottenempire.arma3servergui.server.installation.services.ServerInstallerService;
import java.util.List;
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
public class ServerInstallationController {

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
        List<ServerInstallation> installations = installationService.getAllServerInstallations();
        return ResponseEntity.ok(new ServerInstallationsDto(mapper.map(installations)));
    }

    @GetMapping("/{type}")
    public ResponseEntity<?> getInstallation(@PathVariable ServerType type) {
        ServerInstallation installation = installationService.getServerInstallation(type);
        return ResponseEntity.ok(mapper.map(installation));
    }

    @PostMapping("/{type}")
    public ResponseEntity<?> installOrUpdateServer(@PathVariable ServerType type) {
        installerService.installServer(type, "creatordlc");
        ServerInstallation installation = installationService.getServerInstallation(type);
        return ResponseEntity.ok(mapper.map(installation));
    }

}
