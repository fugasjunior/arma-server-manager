package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.ServerStatus;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/server")
public class ServerController {
    private ArmaServerService serverService;
    private JsonDbService<ServerSettings> settingsDb;
    private JsonDbService<WorkshopMod> modsDb;

    @PostMapping("/start")
    public ResponseEntity startServer() {
        ServerSettings serverSettings = getServerSettings();
        serverService.startServer(serverSettings);
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/stop")
    public ResponseEntity stopServer() {
        serverService.shutDownServer();
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/restart")
    public ResponseEntity restartServer() {
        ServerSettings serverSettings = getServerSettings();
        serverService.shutDownServer();
        serverService.startServer(serverSettings);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/mods")
    public ResponseEntity<Collection<WorkshopMod>> getActiveMods() {
        ServerSettings serverSettings = getServerSettings();
        return new ResponseEntity<>(serverSettings.getMods(), HttpStatus.OK);
    }

    @PostMapping("/mods")
    public ResponseEntity<Collection<WorkshopMod>> setActiveMods(@RequestParam(required = false) List<Long> mods) {
        ServerSettings serverSettings = getServerSettings();

        Set<WorkshopMod> activeMods = serverSettings.getMods();
        activeMods.clear();

        if (mods != null && !mods.isEmpty()) {
            mods.forEach(id -> {
                WorkshopMod mod = modsDb.find(id, WorkshopMod.class);
                if (mod != null && mod.isInstalled()) {
                    activeMods.add(mod);
                }
            });
        }

        settingsDb.save(serverSettings, ServerSettings.class);
        return new ResponseEntity<>(activeMods, HttpStatus.OK);
    }

    @GetMapping("/status")
    public ResponseEntity<ServerStatus> getServerStatus() {
        return new ResponseEntity<>(serverService.getServerStatus(), HttpStatus.OK);
    }

    private ServerSettings getServerSettings() {
        ServerSettings serverSettings = settingsDb.find(Constants.SERVER_MAIN_ID, ServerSettings.class);
        if (serverSettings == null) {
            serverSettings = new ServerSettings();
            serverSettings.setId(Constants.SERVER_MAIN_ID);
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
