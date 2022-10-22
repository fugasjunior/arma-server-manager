package cz.forgottenempire.arma3servergui.server.controllers;

import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.server.ServerInstanceInfo;
import cz.forgottenempire.arma3servergui.server.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.server.dtos.ServerInstanceInfoDto;
import cz.forgottenempire.arma3servergui.server.dtos.ServersDto;
import cz.forgottenempire.arma3servergui.server.entities.Server;
import cz.forgottenempire.arma3servergui.server.mappers.ServerMapper;
import cz.forgottenempire.arma3servergui.server.services.ArmaServerService;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/server")
@Validated
public class ServerController {

    private ArmaServerService serverService;

    ServerMapper serverMapper = Mappers.getMapper(ServerMapper.class);

    @GetMapping
    public ResponseEntity<ServersDto> getAllServers() {
        List<ServerDto> serverDtos = serverService.getAllServers()
                .stream()
                .map(server -> serverMapper.serverToServerDto(server)).toList();
        serverDtos.forEach(s -> {
            ServerInstanceInfo instanceInfo = serverService.getServerInstanceInfo(s.getId());
            ServerInstanceInfoDto instanceInfoDto = serverMapper.serverInstanceInfoToServerInstanceInfoDto(
                    instanceInfo);
            s.setInstanceInfo(instanceInfoDto);
        });
        return ResponseEntity.ok(new ServersDto(serverDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerDto> getServer(@PathVariable Long id) {
        Server server = serverService.getServer(id)
                .orElseThrow(
                        () -> new NotFoundException("Server ID " + id + " doesn't exist"));
        ServerDto serverDto = serverMapper.serverToServerDto(server);
        ServerInstanceInfo instanceInfo = serverService.getServerInstanceInfo(id);
        serverDto.setInstanceInfo(serverMapper.serverInstanceInfoToServerInstanceInfoDto(instanceInfo));
        return ResponseEntity.ok(serverDto);
    }

    @PostMapping
    public ResponseEntity<ServerDto> createServer(@Valid @RequestBody ServerDto serverDto) {
        Server server = serverMapper.serverDtoToServer(serverDto);
        server = serverService.createServer(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(serverMapper.serverToServerDto(server));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServerDto> updateServer(@PathVariable Long id, @Valid @RequestBody ServerDto serverDto) {
        Server server = serverService.getServer(id)
                .orElseThrow(
                        () -> new NotFoundException("Server ID " + id + " doesn't exist"));
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

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startServer(@PathVariable Long id) {
        log.info("Received request to start server ID {}", id);
        serverService.startServer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stopServer(@PathVariable Long id) {
        log.info("Received request to stop server ID {}", id);
        serverService.shutDownServer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/restart")
    public ResponseEntity<?> restartServer(@PathVariable Long id) {
        log.info("Received request to restart server ID {}", id);
        serverService.restartServer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setServerService(ArmaServerService serverService) {
        this.serverService = serverService;
    }
}
