package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.dtos.ServerDetails;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.SettingsService;
import cz.forgottenempire.arma3servergui.services.SystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
@Slf4j
public class SystemController {

    @Value("${hostName}")
    private String hostName;

    private SettingsService settingsService;
    private ArmaServerService serverService;
    private SystemService systemService;

    @GetMapping("/space")
    public ResponseEntity<Long> getSpaceLeftOnDevice() {
        return new ResponseEntity<>(systemService.getDiskSpaceLeft(), HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<ServerDetails> getServerDetails() {
        ServerDetails details = new ServerDetails();
        ServerSettings settings = settingsService.getServerSettings();

        details.setUpdating(serverService.isServerUpdating());
        details.setHostName(hostName);
        details.setPort(settings.getPort());

        details.setSpaceLeft(systemService.getDiskSpaceLeft());
        details.setSpaceTotal(systemService.getDiskSpaceTotal());
        details.setMemoryLeft(systemService.getMemoryLeft());
        details.setMemoryTotal(systemService.getMemoryTotal());
        details.setCpuUsage(systemService.getCpuUsage());

        return ResponseEntity.ok(details);
    }

    @Autowired
    public void setServerService(ArmaServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Autowired
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }
}
