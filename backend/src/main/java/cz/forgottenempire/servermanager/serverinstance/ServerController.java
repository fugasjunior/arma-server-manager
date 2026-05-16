package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.api.ServersApi;
import cz.forgottenempire.servermanager.api.model.AutomaticRestartDto;
import cz.forgottenempire.servermanager.api.model.ServerDto;
import cz.forgottenempire.servermanager.api.model.ServerInstanceInfoDto;
import cz.forgottenempire.servermanager.api.model.ServersDto;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessService;
import java.io.IOException;
import java.time.LocalTime;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
class ServerController implements ServersApi {

    private final ServerInstanceService serverInstanceService;
    private final ServerProcessService serverProcessService;
    private final ServerMapper serverMapper;
    private final PathsFactory pathsFactory;

    @Autowired
    public ServerController(
            ServerInstanceService serverInstanceService,
            ServerProcessService serverProcessService,
            ServerMapper serverMapper,
            PathsFactory pathsFactory) {
        this.serverInstanceService = serverInstanceService;
        this.serverProcessService = serverProcessService;
        this.serverMapper = serverMapper;
        this.pathsFactory = pathsFactory;
    }

    @Override
    public ResponseEntity<ServersDto> getServers() {
        List<ServerDto> serverDtos = serverInstanceService.getAllServers()
                .stream()
                .map(serverMapper::mapServerToDto).toList();
        return ResponseEntity.ok(new ServersDto().servers(serverDtos));
    }

    @Override
    public ResponseEntity<ServerDto> getServer(Long id) {
        Server server = getServerEntity(id);
        return ResponseEntity.ok(serverMapper.mapServerToDto(server));
    }

    @Override
    public ResponseEntity<ServerDto> createServer(ServerDto serverDto) {
        Server server = serverMapper.mapServerDtoToEntity(serverDto);
        server.setId(null);
        server = serverInstanceService.createServer(server);
        return ResponseEntity.status(HttpStatus.CREATED).body(serverMapper.mapServerToDto(server));
    }

    @Override
    public ResponseEntity<ServerDto> updateServer(Long id, ServerDto serverDto) {
        Server server = getServerEntity(id);
        serverMapper.updateServerFromDto(serverDto, server);
        server = serverInstanceService.updateServer(server);
        return ResponseEntity.ok(serverMapper.mapServerToDto(server));
    }

    @Override
    public ResponseEntity<Void> deleteServer(Long id) {
        serverInstanceService.getServer(id).ifPresent(serverInstanceService::deleteServer);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> startServer(Long id) {
        log.info("Received request to start server ID {}", id);
        serverProcessService.startServer(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> stopServer(Long id) {
        log.info("Received request to stop server ID {}", id);
        serverProcessService.shutDownServer(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> restartServer(Long id) {
        log.info("Received request to restart server ID {}", id);
        serverProcessService.restartServer(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<ServerInstanceInfoDto> getServerStatus(Long id) {
        ServerInstanceInfo instanceInfo = Optional.ofNullable(serverProcessService.getServerInstanceInfo(id))
                .orElse(ServerInstanceInfo.builder().build());
        return ResponseEntity.ok(serverMapper.mapServerInstanceInfoToDto(instanceInfo));
    }

    @Override
    public ResponseEntity<Resource> downloadServerLog(Long id) {
        Server server = getServerEntity(id);
        try {
            Resource resource = server.getLog(pathsFactory).asResource()
                    .orElseThrow(() -> new NotFoundException("Log file for server '" + server.getName() + "' doesn't exist"));

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + resource.getFile().getName());
            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(resource.contentLength())
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(resource);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read log file for server '" + server.getName() + "'", e);
        }
    }

    @Override
    public ResponseEntity<String> getServerLog(Long id, Integer count) {
        Server server = getServerEntity(id);
        return ResponseEntity.ok(server.getLog(pathsFactory).getLastLines(count));
    }

    @Override
    public ResponseEntity<Void> setAutoRestart(Long id, AutomaticRestartDto automaticRestartDto) {
        Server server = getServerEntity(id);
        boolean enabled = Boolean.TRUE.equals(automaticRestartDto.getEnabled());
        LocalTime time = automaticRestartDto.getTime() != null ? LocalTime.parse(automaticRestartDto.getTime()) : null;
        serverInstanceService.setAutomaticRestart(server, enabled, time);

        if (enabled) {
            serverProcessService.enableAutoRestart(id, time);
        } else {
            serverProcessService.disableAutoRestart(id);
        }

        return ResponseEntity.ok().build();
    }

    private Server getServerEntity(long id) {
        return serverInstanceService.getServer(id)
                .orElseThrow(() -> new NotFoundException("Server ID " + id + " doesn't exist"));
    }
}
