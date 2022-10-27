package cz.forgottenempire.arma3servergui.server.installation.controllers;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.server.installation.services.ServerInstallationService;
import cz.forgottenempire.arma3servergui.server.installation.services.ServerInstallerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/server/installation/")
public class ServerInstallationController {

    private final ServerInstallerService installerService;

    @Autowired
    public ServerInstallationController(
            ServerInstallerService installerService
    ) {
        this.installerService = installerService;
    }

    @GetMapping
    public ResponseEntity<?> getAllInstalations() {
        return ResponseEntity.ok("TODO");
    }

    @GetMapping("/{type}")
    public ResponseEntity<?> getInstallation(@PathVariable ServerType type) {
        return ResponseEntity.ok("TODO");
    }

    @PostMapping("/{type}")
    public ResponseEntity<?> installOrUpdateServer(@PathVariable ServerType type) {
        installerService.installServer(type, "creatordlc");
        return ResponseEntity.ok("ok"); // TODO
    }

}
