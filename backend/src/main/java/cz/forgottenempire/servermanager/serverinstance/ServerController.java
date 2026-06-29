package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.api.ServersApi;
import cz.forgottenempire.servermanager.api.model.AutoStartDto;
import cz.forgottenempire.servermanager.api.model.AutomaticRestartDto;
import cz.forgottenempire.servermanager.api.model.ConfigOverrideDto;
import cz.forgottenempire.servermanager.api.model.SeedConfigOverrideRequest;
import cz.forgottenempire.servermanager.api.model.ServerDto;
import cz.forgottenempire.servermanager.api.model.ServerInstanceInfoDto;
import cz.forgottenempire.servermanager.api.model.ServersDto;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.Arma3InstanceDataCopier;
import cz.forgottenempire.servermanager.serverinstance.entities.DayZServer;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessService;
import cz.forgottenempire.servermanager.serverinstance.entities.ServerConfigOverride;
import java.io.IOException;
import java.time.LocalTime;

import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
class ServerController implements ServersApi {

    private final ServerInstanceService serverInstanceService;
    private final ServerProcessService serverProcessService;
    private final ServerMapper serverMapper;
    private final PathsFactory pathsFactory;
    private final ServerSecretsMasker secretsMasker;
    private final ConfigOverrideService configOverrideService;
    private final Arma3InstanceDataCopier arma3InstanceDataCopier;

    @Autowired
    public ServerController(
            ServerInstanceService serverInstanceService,
            ServerProcessService serverProcessService,
            ServerMapper serverMapper,
            PathsFactory pathsFactory,
            ServerSecretsMasker secretsMasker,
            ConfigOverrideService configOverrideService,
            Arma3InstanceDataCopier arma3InstanceDataCopier) {
        this.serverInstanceService = serverInstanceService;
        this.serverProcessService = serverProcessService;
        this.serverMapper = serverMapper;
        this.pathsFactory = pathsFactory;
        this.secretsMasker = secretsMasker;
        this.configOverrideService = configOverrideService;
        this.arma3InstanceDataCopier = arma3InstanceDataCopier;
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_VIEW + "')")
    public ResponseEntity<ServersDto> getServers() {
        List<ServerDto> serverDtos = serverInstanceService.getAllServers()
                .stream()
                .map(server -> {
                    ServerDto dto = serverMapper.mapServerToDto(server);
                    attachOverrides(server, dto);
                    return dto;
                })
                .peek(secretsMasker::maskIfUnauthorized)
                .toList();
        return ResponseEntity.ok(new ServersDto().servers(serverDtos));
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_VIEW + "')")
    public ResponseEntity<ServerDto> getServer(Long id) {
        Server server = getServerEntity(id);
        ServerDto dto = serverMapper.mapServerToDto(server);
        attachOverrides(server, dto);
        secretsMasker.maskIfUnauthorized(dto);
        return ResponseEntity.ok(dto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_MODIFY + "')")
    public ResponseEntity<ServerDto> createServer(ServerDto serverDto) {
        List<ConfigOverrideDto> overrides = extractOverridesFromDto(serverDto);
        Server server = serverMapper.mapServerDtoToEntity(serverDto);
        server.setId(null);
        server = serverInstanceService.createServer(server, overrides);
        ServerDto dto = serverMapper.mapServerToDto(server);
        attachOverrides(server, dto);
        secretsMasker.maskIfUnauthorized(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_MODIFY + "')")
    public ResponseEntity<ServerDto> duplicateServer(Long id) {
        Server source = getServerEntity(id);
        Long sourceId = source.getId();
        ServerDto sourceDto = serverMapper.mapServerToDto(source);
        attachOverrides(source, sourceDto);
        Server clone = serverMapper.mapServerDtoToEntity(sourceDto);
        clone.setId(null);
        clone.setName(source.getName() + " (copy)");
        List<ConfigOverrideDto> overrides = extractOverridesFromDto(sourceDto);
        Server created = serverInstanceService.createServer(clone, overrides);
        if (source instanceof Arma3Server) {
            arma3InstanceDataCopier.copyInstanceData(sourceId, created.getId());
        }
        ServerDto dto = serverMapper.mapServerToDto(created);
        secretsMasker.maskIfUnauthorized(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @Override
    @Transactional
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_MODIFY + "')")
    public ResponseEntity<ServerDto> updateServer(Long id, ServerDto serverDto) {
        List<ConfigOverrideDto> overrides = extractOverridesFromDto(serverDto);
        Server existing = getServerEntity(id);
        if (!secretsMasker.canViewSecrets()) {
            preserveExistingPasswords(serverDto, existing);
            overrides = preserveExistingOverrides(id);
        }
        clearModCollections(existing);
        serverInstanceService.flush();
        serverMapper.updateServerFromDto(serverDto, existing);
        Server server = serverInstanceService.updateServer(existing, overrides);
        ServerDto dto = serverMapper.mapServerToDto(server);
        attachOverrides(server, dto);
        secretsMasker.maskIfUnauthorized(dto);
        return ResponseEntity.ok(dto);
    }

    private void clearModCollections(Server server) {
        if (server instanceof Arma3Server a3) {
            a3.getActiveMods().clear();
            a3.getActiveLocalMods().clear();
        } else if (server instanceof DayZServer dz) {
            dz.getActiveMods().clear();
            dz.getActiveLocalMods().clear();
        }
    }

    private List<ConfigOverrideDto> preserveExistingOverrides(long id) {
        return serverMapper.mapOverridesToDtos(configOverrideService.getServerOverrides(id));
    }

    private void preserveExistingPasswords(ServerDto serverDto, Server existing) {
        if (serverDto instanceof cz.forgottenempire.servermanager.api.model.Arma3ServerDto a3) {
            a3.setPassword(existing.getPassword());
            a3.setAdminPassword(existing.getAdminPassword());
        } else if (serverDto instanceof cz.forgottenempire.servermanager.api.model.DayZServerDto dz) {
            dz.setPassword(existing.getPassword());
            dz.setAdminPassword(existing.getAdminPassword());
        } else if (serverDto instanceof cz.forgottenempire.servermanager.api.model.ReforgerServerDto rf) {
            rf.setPassword(existing.getPassword());
            rf.setAdminPassword(existing.getAdminPassword());
        }
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_DELETE + "')")
    public ResponseEntity<Void> deleteServer(Long id) {
        serverInstanceService.getServer(id).ifPresent(serverInstanceService::deleteServer);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_OPERATE + "')")
    public ResponseEntity<Void> startServer(Long id) {
        log.info("Received request to start server ID {}", id);
        serverProcessService.startServer(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_OPERATE + "')")
    public ResponseEntity<Void> stopServer(Long id) {
        log.info("Received request to stop server ID {}", id);
        serverProcessService.shutDownServer(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_OPERATE + "')")
    public ResponseEntity<Void> restartServer(Long id) {
        log.info("Received request to restart server ID {}", id);
        serverProcessService.restartServer(id);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_VIEW + "')")
    public ResponseEntity<ServerInstanceInfoDto> getServerStatus(Long id) {
        ServerInstanceInfo instanceInfo = Optional.ofNullable(serverProcessService.getServerInstanceInfo(id))
                .orElse(ServerInstanceInfo.builder().build());
        return ResponseEntity.ok(serverMapper.mapServerInstanceInfoToDto(instanceInfo));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_LOGS_VIEW + "')")
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
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_LOGS_VIEW + "')")
    public ResponseEntity<String> getServerLog(Long id, Integer count) {
        Server server = getServerEntity(id);
        return ResponseEntity.ok(server.getLog(pathsFactory).getLastLines(count));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_MODIFY + "')")
    public ResponseEntity<Void> setServerAutoStart(Long id, AutoStartDto autoStartDto) {
        Server server = getServerEntity(id);
        serverInstanceService.setAutoStart(server, Boolean.TRUE.equals(autoStartDto.getEnabled()));
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_OPERATE + "')")
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

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.ADVANCED_CONFIG_EDIT + "') and hasAuthority('" + PermissionCode.SERVER_SECRETS_VIEW + "')")
    public ResponseEntity<ConfigOverrideDto> seedConfigOverride(SeedConfigOverrideRequest request) {
        ConfigFileKey configKey = ConfigFileKey.valueOf(request.getConfigKey());
        ServerDto draft = request.getServer();

        Long serverId = null;
        String serverName = "new";
        if (draft instanceof cz.forgottenempire.servermanager.api.model.DayZServerDto dz) {
            serverId = dz.getId();
            serverName = dz.getName();
        } else if (draft instanceof cz.forgottenempire.servermanager.api.model.Arma3ServerDto a3) {
            serverId = a3.getId();
            serverName = a3.getName();
        } else if (draft instanceof cz.forgottenempire.servermanager.api.model.ReforgerServerDto rf) {
            serverId = rf.getId();
            serverName = rf.getName();
        }

        log.info("Seed config override: server='{}'(id={}) key={}", serverName, serverId, configKey);

        Server transientEntity = serverMapper.mapServerDtoToEntity(draft);
        ConfigOverrideDto result = configOverrideService.seedConfigOverride(configKey, serverId, transientEntity);
        return ResponseEntity.ok(result);
    }

    private Server getServerEntity(long id) {
        return serverInstanceService.getServer(id)
                .orElseThrow(() -> new NotFoundException("Server ID " + id + " doesn't exist"));
    }

    private void attachOverrides(Server server, ServerDto dto) {
        List<ServerConfigOverride> overrides = configOverrideService.getServerOverrides(server.getId());
        if (overrides.isEmpty()) {
            setOverridesNull(dto);
            return;
        }
        List<ConfigOverrideDto> dtos = serverMapper.mapOverridesToDtos(overrides);
        if (dto instanceof cz.forgottenempire.servermanager.api.model.Arma3ServerDto a3) {
            a3.setConfigOverrides(dtos);
        } else if (dto instanceof cz.forgottenempire.servermanager.api.model.DayZServerDto dz) {
            dz.setConfigOverrides(dtos);
        } else if (dto instanceof cz.forgottenempire.servermanager.api.model.ReforgerServerDto rf) {
            rf.setConfigOverrides(dtos);
        }
    }

    private void setOverridesNull(ServerDto dto) {
        if (dto instanceof cz.forgottenempire.servermanager.api.model.Arma3ServerDto a3) {
            a3.setConfigOverrides(null);
        } else if (dto instanceof cz.forgottenempire.servermanager.api.model.DayZServerDto dz) {
            dz.setConfigOverrides(null);
        } else if (dto instanceof cz.forgottenempire.servermanager.api.model.ReforgerServerDto rf) {
            rf.setConfigOverrides(null);
        }
    }

    private List<ConfigOverrideDto> extractOverridesFromDto(ServerDto dto) {
        if (dto instanceof cz.forgottenempire.servermanager.api.model.Arma3ServerDto a3
                && a3.getConfigOverrides() != null) {
            return a3.getConfigOverrides();
        } else if (dto instanceof cz.forgottenempire.servermanager.api.model.DayZServerDto dz
                && dz.getConfigOverrides() != null) {
            return dz.getConfigOverrides();
        } else if (dto instanceof cz.forgottenempire.servermanager.api.model.ReforgerServerDto rf
                && rf.getConfigOverrides() != null) {
            return rf.getConfigOverrides();
        }
        return List.of();
    }
}
