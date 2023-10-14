package cz.forgottenempire.servermanager.serverinstance;

import com.google.common.base.Joiner;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.exceptions.PortAlreadyTakenException;
import cz.forgottenempire.servermanager.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
class ServerProcessService {

    private final ServerRepository serverRepository;
    private final ServerInstanceInfoRepository instanceInfoRepository;
    private final ServerProcessRepository processRepository;
    private final ConfigFileService configFileService;
    private final PathsFactory pathsFactory;

    @Autowired
    public ServerProcessService(
            ServerRepository serverRepository,
            ServerInstanceInfoRepository instanceInfoRepository,
            ServerProcessRepository processRepository, ConfigFileService configFileService,
            PathsFactory pathsFactory
    ) {
        this.serverRepository = serverRepository;
        this.instanceInfoRepository = instanceInfoRepository;
        this.processRepository = processRepository;
        this.configFileService = configFileService;
        this.pathsFactory = pathsFactory;

        addShutdownHook(instanceInfoRepository);
    }

    public void startServer(Long id) {
        Server server = serverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Server ID " + id + " not found"));

        ServerProcess serverProcess = processRepository.get(id)
                .orElseGet(() -> {
                    ServerProcess process = server.getProcess();
                    processRepository.store(id, process);
                    return process;
                });

        if (serverProcess.isAlive()) {
            return;
        }

        validatePortsNotTaken(server);

        ServerInstanceInfo instanceInfo = instanceInfoRepository.getServerInstanceInfo(id);
        if (isServerInstanceRunning(instanceInfo)) {
            log.info("Server '{}' (ID {}) is already running", server.getName(), id);
        }

        writeConfigFiles(server);

        Process process = serverProcess.start();
        instanceInfo = ServerInstanceInfo.builder()
                .id(id)
                .alive(true)
                .startedAt(LocalDateTime.now())
                .maxPlayers(server.getMaxPlayers())
                .process(process)
                .build();
        instanceInfoRepository.storeServerInstanceInfo(id, instanceInfo);
    }

    public void shutDownServer(Long id) {
        ServerInstanceInfo instanceInfo = instanceInfoRepository.getServerInstanceInfo(id);
        Process process = instanceInfo.getProcess();
        process.destroy();

        instanceInfo = ServerInstanceInfo.builder()
                .id(id)
                .alive(false)
                .build();
        instanceInfoRepository.storeServerInstanceInfo(id, instanceInfo);
    }

    public void restartServer(Long id) {
        shutDownServer(id);
        startServer(id);
    }

    public ServerInstanceInfo getServerInstanceInfo(Long id) {
        return instanceInfoRepository.getServerInstanceInfo(id);
    }

    public boolean isServerInstanceRunning(Server server) {
        return isServerInstanceRunning(getServerInstanceInfo(server.getId()));
    }

    private static void addShutdownHook(ServerInstanceInfoRepository instanceInfoRepository) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> instanceInfoRepository.getAll().stream()
                .map(ServerInstanceInfo::getProcess)
                .filter(Objects::nonNull)
                .filter(Process::isAlive)
                .forEach(Process::destroy)));
    }

    private void validatePortsNotTaken(Server server) {
        serverRepository.findAllByPortOrQueryPort(server.getPort(), server.getQueryPort()).stream()
                .filter(s -> !s.equals(server))
                .forEach(s -> {
                    ServerInstanceInfo instanceInfo = instanceInfoRepository.getServerInstanceInfo(s.getId());
                    if (instanceInfo.isAlive()) {
                        int conflictingPort = s.getPort() == server.getPort() ?
                                server.getPort()
                                : server.getQueryPort();
                        String errorMessage = String.format("Port conflict: Server '%s' already uses port %d.",
                                s.getName(), conflictingPort);
                        log.error("Server '{}' (ID {}) could not be started because of port conflict (port {})"
                                        + " with server '{}' (ID {})",
                                server.getName(), server.getId(), conflictingPort, s.getName(), s.getId());
                        throw new PortAlreadyTakenException(errorMessage);
                    }
                });
    }

    private boolean isServerInstanceRunning(ServerInstanceInfo instanceInfo) {
        return instanceInfo.isAlive() && instanceInfo.getProcess().isAlive();
    }

    private void writeConfigFiles(Server server) {
        boolean configRegenerationNeeded = !configFileService.getConfigFileForServer(server).exists();
        if (server.getType() == ServerType.ARMA3) {
            configRegenerationNeeded = configRegenerationNeeded || !pathsFactory.getServerProfileFile(server.getId()).exists();
        }
        if (configRegenerationNeeded) {
            configFileService.writeConfig(server);
        }
    }
}
