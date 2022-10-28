package cz.forgottenempire.arma3servergui.server.serverinstance.controllers;

import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.server.ServerInstanceInfo;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.Arma3ServerDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.DayZServerDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.ServerInstanceInfoDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.dtos.ServersDto;
import cz.forgottenempire.arma3servergui.server.serverinstance.entities.Arma3Server;
import cz.forgottenempire.arma3servergui.server.serverinstance.entities.DayZServer;
import cz.forgottenempire.arma3servergui.server.serverinstance.entities.Server;
import cz.forgottenempire.arma3servergui.server.serverinstance.mappers.ServerMapper;
import cz.forgottenempire.arma3servergui.server.serverinstance.services.ServerInstanceService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@Slf4j
@RequestMapping("/api/server")
@Validated
public class ServerController {

    private ServerInstanceService serverInstanceService;

    ServerMapper serverMapper = Mappers.getMapper(ServerMapper.class);

    @GetMapping
    public ResponseEntity<ServersDto> getAllServers() {
        List<ServerDto> serverDtos = serverInstanceService.getAllServers()
                .stream()
                .map(server -> serverMapper.mapServerToDto(server)).toList();
        serverDtos.forEach(s -> {
            ServerInstanceInfo instanceInfo = serverInstanceService.getServerInstanceInfo(s.getId());
            ServerInstanceInfoDto instanceInfoDto = serverMapper.mapServerInstanceInfoToDto(
                    instanceInfo);
            s.setInstanceInfo(instanceInfoDto);
        });
        return ResponseEntity.ok(new ServersDto(serverDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServerDto> getServer(@PathVariable Long id) {
        Server server = serverInstanceService.getServer(id)
                .orElseThrow(
                        () -> new NotFoundException("Server ID " + id + " doesn't exist"));
        ServerDto serverDto = serverMapper.mapServerToDto(server);
        ServerInstanceInfo instanceInfo = serverInstanceService.getServerInstanceInfo(id);
        serverDto.setInstanceInfo(serverMapper.mapServerInstanceInfoToDto(instanceInfo));
        return ResponseEntity.ok(serverDto);
    }

    @PostMapping
    public ResponseEntity<ServerDto> createServer(@Valid @RequestBody ServerDto serverDto) {
        Server server = serverMapper.mapServerDtoToEntity(serverDto);
        server = serverInstanceService.createServer(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(serverMapper.mapServerToDto(server));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServerDto> updateServer(@PathVariable Long id, @Valid @RequestBody ServerDto serverDto) {
        Server server = serverInstanceService.getServer(id)
                .orElseThrow(
                        () -> new NotFoundException("Server ID " + id + " doesn't exist"));
        serverDto.setId(server.getId());
        serverMapper.updateServerFromDto(serverDto, server);
        server = serverInstanceService.updateServer(server);
        return ResponseEntity.ok(serverMapper.mapServerToDto(server));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteServer(@PathVariable Long id) {
        serverInstanceService.getServer(id).ifPresent((server) -> serverInstanceService.deleteServer(server));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startServer(@PathVariable Long id) {
        log.info("Received request to start server ID {}", id);
        serverInstanceService.startServer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stopServer(@PathVariable Long id) {
        log.info("Received request to stop server ID {}", id);
        serverInstanceService.shutDownServer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/restart")
    public ResponseEntity<?> restartServer(@PathVariable Long id) {
        log.info("Received request to restart server ID {}", id);
        serverInstanceService.restartServer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setServerInstanceService(ServerInstanceService serverInstanceService) {
        this.serverInstanceService = serverInstanceService;
    }
}
