package cz.forgottenempire.arma3servergui.server.controllers;

import cz.forgottenempire.arma3servergui.server.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.server.dtos.ServersDto;
import cz.forgottenempire.arma3servergui.server.entities.Server;
import cz.forgottenempire.arma3servergui.server.mappers.ServerMapper;
import cz.forgottenempire.arma3servergui.server.services.ArmaServerService;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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

//    @PostMapping("/start")
//    public ResponseEntity<?> startServer() {
//        log.info("Received request to start server");
//        Server serverSettings = settingsService.getServerSettings();
//        serverService.startServer(serverSettings);
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @PostMapping("/stop")
//    public ResponseEntity<?> stopServer() {
//        log.info("Received request to stop server");
//        serverService.shutDownServer();
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @PostMapping("/{id}/restart")
//    public ResponseEntity<?> restartServer(@PathVariable Long id) {
//        log.info("Received request to restart server");
//        if (!serverService.restartServer(id)) {
//            log.error("Error during server restart!");
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @GetMapping("/query")
//    public ResponseEntity<ServerStatus> getServerStatus() {
//        return new ResponseEntity<>(serverStatus, HttpStatus.OK);
//    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> isServerProcessAlive() {
        return new ResponseEntity<>(serverService.isServerProcessAlive(), HttpStatus.OK);
    }

    @Autowired
    public void setServerService(ArmaServerService serverService) {
        this.serverService = serverService;
    }
}
