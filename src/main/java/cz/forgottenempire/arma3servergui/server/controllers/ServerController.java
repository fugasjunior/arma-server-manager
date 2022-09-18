package cz.forgottenempire.arma3servergui.server.controllers;

import cz.forgottenempire.arma3servergui.server.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.server.dtos.ServersDto;
import cz.forgottenempire.arma3servergui.server.entities.Server;
import cz.forgottenempire.arma3servergui.server.entities.ServerStatus;
import cz.forgottenempire.arma3servergui.server.mappers.ServerMapper;
import cz.forgottenempire.arma3servergui.workshop.entities.SteamAuth;
import cz.forgottenempire.arma3servergui.server.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.server.services.SettingsService;
import cz.forgottenempire.arma3servergui.workshop.services.SteamAuthService;
import java.util.Optional;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@Slf4j
@RequestMapping("/api/server")
public class ServerController {

    private ArmaServerService serverService;
    private SettingsService settingsService;
    private SteamAuthService steamAuthService;
    private ServerStatus serverStatus;

    ServerMapper serverMapper = Mappers.getMapper(ServerMapper.class);

    @GetMapping
    public ResponseEntity<ServersDto> getAllServers() {
        ServersDto serversDto = new ServersDto(serverMapper.serversToServerDtos(serverService.getAllServers()));
        return ResponseEntity.ok(serversDto);
    }
    @GetMapping("/{id}")
    public ResponseEntity<ServerDto> getServer(@PathVariable Long id) {
        Server server = serverService.getServer(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server ID " + id + " doesn't exist"));
        return ResponseEntity.ok(serverMapper.serverToServerDto(server));
    }

    @PostMapping
    public ResponseEntity<ServerDto> createServer(@RequestBody @Valid ServerDto serverDto) {
        Server server = serverMapper.serverDtoToServer(serverDto);
        server = serverService.createServer(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(serverMapper.serverToServerDto(server));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServerDto> updateServer(@PathVariable Long id, @RequestBody @Valid ServerDto serverDto) {
        Server server = serverService.getServer(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Server ID " + id + " doesn't exist"));
        serverDto.setId(server.getId());
        serverMapper.updateServerFromDto(serverDto, server);
        server = serverService.updateServer(server);
        return ResponseEntity.ok(serverMapper.serverToServerDto(server));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServer(@PathVariable Long id) {
        serverService.getServer(id).ifPresent((server) -> serverService.deleteServer(server));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/start")
    public ResponseEntity<?> startServer() {
        log.info("Received request to start server");
        Server serverSettings = settingsService.getServerSettings();
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
    public ResponseEntity<Server> getServerSettings() {
        return new ResponseEntity<>(settingsService.getServerSettings(), HttpStatus.OK);
    }

    @PostMapping("/settings")
    public ResponseEntity<Server> setServerSettings(@RequestBody @Valid Server settings) {
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
