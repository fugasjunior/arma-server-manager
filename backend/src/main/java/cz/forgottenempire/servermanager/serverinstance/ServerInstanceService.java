package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.Arma3KeyService;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.api.model.ConfigOverrideDto;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.exceptions.ModifyingRunningServerException;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class ServerInstanceService {

    private final ServerRepository serverRepository;
    private final ServerProcessService processService;
    private final PathsFactory pathsFactory;
    private final Arma3InstancePaths arma3InstancePaths;
    private final Arma3KeyService arma3KeyService;
    private final FreeMarkerConfigurer freeMarkerConfigurer;
    private final ConfigOverrideService configOverrideService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ServerInstanceService(
            ServerRepository serverRepository,
            ServerProcessService processService,
            PathsFactory pathsFactory,
            Arma3InstancePaths arma3InstancePaths,
            Arma3KeyService arma3KeyService,
            FreeMarkerConfigurer freeMarkerConfigurer,
            ConfigOverrideService configOverrideService
    ) {
        this.serverRepository = serverRepository;
        this.processService = processService;
        this.pathsFactory = pathsFactory;
        this.arma3InstancePaths = arma3InstancePaths;
        this.arma3KeyService = arma3KeyService;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.configOverrideService = configOverrideService;
    }

    public List<Server> getAllServers() {
        return serverRepository.findAll();
    }

    public Optional<Server> getServer(@NotNull Long id) {
        return serverRepository.findById(id);
    }

    public Server createServer(Server server, List<ConfigOverrideDto> overrides) {
        server.getCustomLaunchParameters().forEach(param -> param.setServer(server));
        Server persistedServer = serverRepository.save(server);
        Map<ConfigFileKey, String> overrideMap = configOverrideService.syncOverrides(persistedServer, overrides);
        ServerLaunchContext ctx = new ServerLaunchContext(
                pathsFactory, arma3InstancePaths, arma3KeyService, freeMarkerConfigurer, null,
                overrideMap.isEmpty() ? null : overrideMap);
        persistedServer.getConfigFiles(ctx).forEach(ServerConfig::generate);
        return persistedServer;
    }

    public Server updateServer(Server server, List<ConfigOverrideDto> overrides) {
        if (processService.isServerInstanceRunning(server)) {
            throw new ModifyingRunningServerException("Cannot modify running server '" + server.getName() + "'");
        }
        return createServer(server, overrides);
    }

    public void deleteServer(Server server) {
        if (processService.isServerInstanceRunning(server)) {
            throw new ModifyingRunningServerException("Cannot delete running server '" + server.getName() + "'");
        }
        serverRepository.delete(server);
    }

    public void setAutomaticRestart(Server server, boolean enabled, LocalTime time) {
        server.setRestartAutomatically(enabled);
        server.setAutomaticRestartTime(time);
        serverRepository.save(server);
    }

    public void flush() {
        entityManager.flush();
    }
}
