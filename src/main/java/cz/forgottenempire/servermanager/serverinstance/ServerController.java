package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.serverinstance.dtos.ServerDto;
import cz.forgottenempire.servermanager.serverinstance.dtos.ServerInstanceInfoDto;
import cz.forgottenempire.servermanager.serverinstance.dtos.ServersDto;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/server")
@Slf4j
class ServerController {

    public static final int DEFAULT_LOG_LINES_COUNT = 100;
    private final ServerInstanceService serverInstanceService;
    private final ServerProcessService serverProcessService;
    private final ServerLogsService logsService;
    private final ServerMapper serverMapper = Mappers.getMapper(ServerMapper.class);

    @Autowired
    public ServerController(
            ServerInstanceService serverInstanceService,
            ServerProcessService serverProcessService,
            ServerLogsService logsService
    ) {
        this.serverInstanceService = serverInstanceService;
        this.serverProcessService = serverProcessService;
        this.logsService = logsService;
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
        Server server = getServerEntity(id);
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
        Server server = getServerEntity(id);
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

    @GetMapping("/{id}/log/download")
    public ResponseEntity<Resource> downloadLogFile(@PathVariable long id) throws IOException {
        Server server = getServerEntity(id);

        Resource resource = logsService.getLogFileAsResource(server)
                .orElseThrow(() -> new NotFoundException("Log file for server '" + server.getName() + "' doesn't exist"));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFile().getName());
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.TEXT_PLAIN)
                .body(resource);
    }

    @GetMapping("/{id}/log")
    public ResponseEntity<String> getLastFilesFromLog(@PathVariable long id, @RequestParam(required = false) Integer count) {
        if (count == null) {
            count = DEFAULT_LOG_LINES_COUNT;
        }

        Server server = getServerEntity(id);
        String logLines = logsService.getLastLinesFromServerLog(server, count);
        return ResponseEntity.ok(logLines);
    }

    private Server getServerEntity(long id) {
        return serverInstanceService.getServer(id)
                .orElseThrow(
                        () -> new NotFoundException("Server ID " + id + " doesn't exist"));
    }
}
