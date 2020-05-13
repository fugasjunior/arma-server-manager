package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.dtos.ServerStatus;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@CrossOrigin // TODO testing purposes
@Slf4j
@RequestMapping("/server")
public class ServerController {
    private ArmaServerService serverService;
    private JsonDbService<ServerSettings> settingsDb;
    private JsonDbService<WorkshopMod> modsDb;

    @PostMapping("/start")
    public ResponseEntity<?> startServer() {
        log.info("Received request to start server");
        ServerSettings serverSettings = findServerSettings();
        serverService.startServer(serverSettings);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/stop")
    public ResponseEntity<?> stopServer() {
        log.info("Received request to stop server");
        serverService.shutDownServer(findServerSettings());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/restart")
    public ResponseEntity<?> restartServer() {
        log.info("Received request to restart server");
        ServerSettings serverSettings = findServerSettings();
        serverService.shutDownServer(findServerSettings());
        serverService.startServer(serverSettings);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<ServerStatus> getServerStatus() {
        return new ResponseEntity<>(serverService.getServerStatus(findServerSettings()), HttpStatus.OK);
    }

    @GetMapping("/settings")
    public ResponseEntity<ServerSettings> getServerSettings() {
        return new ResponseEntity<>(findServerSettings(), HttpStatus.OK);
    }

    @PostMapping("/settings")
    public ResponseEntity<ServerSettings> setServerSettings(@RequestBody @Valid ServerSettings settings) {
        log.info("Setting new configuration: {}", settings.toString());

        settingsDb.save(settings, ServerSettings.class);
        return new ResponseEntity<>(findServerSettings(), HttpStatus.OK);
    }

    private ServerSettings findServerSettings() {
        ServerSettings serverSettings = settingsDb.find(Constants.SERVER_MAIN_ID, ServerSettings.class);

        // DEBUG if server settings don't exist, generate default one
        if (serverSettings == null) {
            serverSettings = new ServerSettings();
            serverSettings.setId(Constants.SERVER_MAIN_ID);
            serverSettings.setName("Test server - DEFAULT");
            serverSettings.setMaxPlayers(10);
            serverSettings.setPort(2302);
            settingsDb.save(serverSettings, ServerSettings.class);
        }

        return serverSettings;
    }

    @Autowired
    public void setServerService(ArmaServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setSettingsDb(JsonDbService<ServerSettings> settingsDb) {
        this.settingsDb = settingsDb;
    }

    @Autowired
    public void setModsDb(JsonDbService<WorkshopMod> modsDb) {
        this.modsDb = modsDb;
    }
}
