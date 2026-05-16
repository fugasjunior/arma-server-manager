package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.exceptions.ModifyingRunningServerException;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ServerInstanceService {

    private final ServerRepository serverRepository;
    private final ServerProcessService processService;
    private final PathsFactory pathsFactory;
    private final FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    public ServerInstanceService(
            ServerRepository serverRepository,
            ServerProcessService processService,
            PathsFactory pathsFactory,
            FreeMarkerConfigurer freeMarkerConfigurer
    ) {
        this.serverRepository = serverRepository;
        this.processService = processService;
        this.pathsFactory = pathsFactory;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }

    public List<Server> getAllServers() {
        return serverRepository.findAll();
    }

    public Optional<Server> getServer(@NotNull Long id) {
        return serverRepository.findById(id);
    }

    public Server createServer(Server server) {
        server.getCustomLaunchParameters().forEach(param -> param.setServer(server));
        Server persistedServer = serverRepository.save(server);
        ServerLaunchContext ctx = new ServerLaunchContext(pathsFactory, freeMarkerConfigurer);
        persistedServer.getConfigFiles(ctx).forEach(ServerConfig::generate);
        return persistedServer;
    }

    public Server updateServer(Server server) {
        if (processService.isServerInstanceRunning(server)) {
            throw new ModifyingRunningServerException("Cannot modify running server '" + server.getName() + "'");
        }
        return createServer(server);
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
}
