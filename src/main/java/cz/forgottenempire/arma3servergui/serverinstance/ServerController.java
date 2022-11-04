package cz.forgottenempire.arma3servergui.serverinstance;

import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.serverinstance.dtos.ServerDto;
import cz.forgottenempire.arma3servergui.serverinstance.dtos.ServerInstanceInfoDto;
import cz.forgottenempire.arma3servergui.serverinstance.dtos.ServersDto;
import cz.forgottenempire.arma3servergui.serverinstance.entities.Server;
import java.util.List;
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

@RestController
@RequestMapping("/api/server")
@Slf4j
class ServerController {

    private final ServerInstanceService serverInstanceService;
    private final ServerProcessService serverProcessService;
    private final ServerMapper serverMapper = Mappers.getMapper(ServerMapper.class);

    @Autowired
    public ServerController(ServerInstanceService serverInstanceService, ServerProcessService serverProcessService) {
        this.serverInstanceService = serverInstanceService;
        this.serverProcessService = serverProcessService;
    }

    @GetMapping
    public ResponseEntity<ServersDto> getAllServers() {
        List<ServerDto> serverDtos = serverInstanceService.getAllServers()
                .stream()
                .map(serverMapper::mapServerToDto).toList();
        serverDtos.forEach(s -> {
            ServerInstanceInfo instanceInfo = serverProcessService.getServerInstanceInfo(s.getId());
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
        ServerInstanceInfo instanceInfo = serverProcessService.getServerInstanceInfo(id);
        serverDto.setInstanceInfo(serverMapper.mapServerInstanceInfoToDto(instanceInfo));
        return ResponseEntity.ok(serverDto);
    }

    @PostMapping
    public ResponseEntity<ServerDto> createServer(@Valid @RequestBody ServerDto serverDto) {
        serverDto.setId(null);
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
        serverInstanceService.getServer(id).ifPresent(serverInstanceService::deleteServer);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<?> startServer(@PathVariable Long id) {
        log.info("Received request to start server ID {}", id);
        serverProcessService.startServer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stopServer(@PathVariable Long id) {
        log.info("Received request to stop server ID {}", id);
        serverProcessService.shutDownServer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{id}/restart")
    public ResponseEntity<?> restartServer(@PathVariable Long id) {
        log.info("Received request to restart server ID {}", id);
        serverProcessService.restartServer(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
