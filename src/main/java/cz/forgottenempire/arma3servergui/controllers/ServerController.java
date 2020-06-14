package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.ServerStatus;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
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
@Slf4j
@RequestMapping("/api/server")
public class ServerController {
    private ArmaServerService serverService;
    private JsonDbService<ServerSettings> settingsDb;
    private JsonDbService<WorkshopMod> modsDb;
    private JsonDbService<SteamAuth> authDb;
    private ServerStatus serverStatus;

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
        serverService.shutDownServer();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/restart")
    public ResponseEntity<?> restartServer() {
        log.info("Received request to restart server");
        if (!serverService.restartServer(findServerSettings())) {
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
        return new ResponseEntity<>(findServerSettings(), HttpStatus.OK);
    }

    @PostMapping("/settings")
    public ResponseEntity<ServerSettings> setServerSettings(@RequestBody @Valid ServerSettings settings) {
        log.info("Setting new configuration: {}", settings.toString());

        settingsDb.save(settings, ServerSettings.class);
        return new ResponseEntity<>(findServerSettings(), HttpStatus.OK);
    }

    @Autowired
    public void setServerStatus(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
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

    @Autowired
    public void setAuthDb(JsonDbService<SteamAuth> authDb) {
        this.authDb = authDb;
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateServer() {
        if (serverService.isServerUpdating()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        SteamAuth auth = authDb.find(Constants.ACCOUND_DEFAULT_ID, SteamAuth.class);
        if (auth == null) {
            return new ResponseEntity<>("No steam authentication set", HttpStatus.BAD_REQUEST);
        }

        serverService.updateServer(auth);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
