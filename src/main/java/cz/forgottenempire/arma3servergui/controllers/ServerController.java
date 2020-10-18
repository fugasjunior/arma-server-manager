package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.ServerStatus;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.SettingsService;
import cz.forgottenempire.arma3servergui.services.SteamAuthService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/server")
public class ServerController {

    private ArmaServerService serverService;
    private SettingsService settingsService;
    private SteamAuthService steamAuthService;
    private ServerStatus serverStatus;

    @PostMapping("/start")
    public ResponseEntity<?> startServer() {
        log.info("Received request to start server");
        ServerSettings serverSettings = settingsService.getServerSettings();
        serverService.startServer(serverSettings);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stopServer() {
        log.info("Received request to stop server");
        serverService.shutDownServer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/restart")
    public ResponseEntity<?> restartServer() {
        log.info("Received request to restart server");
        if (!serverService.restartServer(settingsService.getServerSettings())) {
            log.error("Error during server restart!");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/query")
    public ResponseEntity<ServerStatus> getServerStatus() {
        return new ResponseEntity<>(serverStatus, HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> isServerProcessAlive() {
        return new ResponseEntity<>(serverService.isServerProcessAlive(), HttpStatus.OK);
    }

    @GetMapping("/settings")
    public ResponseEntity<ServerSettings> getServerSettings() {
        return new ResponseEntity<>(settingsService.getServerSettings(), HttpStatus.OK);
    }

    @PostMapping("/settings")
    public ResponseEntity<ServerSettings> setServerSettings(@RequestBody @Valid ServerSettings settings) {
        log.info("Setting new configuration: {}", settings.toString());

        settingsService.setServerSettings(settings);
        return new ResponseEntity<>(settingsService.getServerSettings(), HttpStatus.OK);
    }

    @Autowired
    public void setServerStatus(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateServer() {
        if (serverService.isServerUpdating()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        SteamAuth auth = steamAuthService.getAuthAccount();
        if (StringUtils.isBlank(auth.getUsername()) || StringUtils.isBlank(auth.getPassword())) {
            return new ResponseEntity<>("No steam authentication set", HttpStatus.BAD_REQUEST);
        }

        serverService.updateServer(auth);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Autowired
    public void setServerService(ArmaServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setSteamAuthService(SteamAuthService steamAuthService) {
        this.steamAuthService = steamAuthService;
    }
}
