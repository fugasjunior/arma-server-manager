package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.serverinstance.ServerInstanceInfo;
import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.exceptions.PortAlreadyTakenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@Slf4j
public
class ServerProcessService {

    private final ServerRepository serverRepository;
    private final ServerProcessRepository processRepository;

    @Autowired
    public ServerProcessService(
            ServerRepository serverRepository,
            ServerProcessRepository processRepository
    ) {
        this.serverRepository = serverRepository;
        this.processRepository = processRepository;
        addShutdownHook(processRepository);
    }

    public void startServer(Long id) {
        Server server = getServer(id);

        ServerProcess serverProcess = getServerProcess(server);
        if (serverProcess.isAlive()) {
            return;
        }

        validatePortsNotTaken(server);

        serverProcess.start();
    }

    public void shutDownServer(Long id) {
        Server server = getServer(id);
        ServerProcess serverProcess = getServerProcess(server);
        serverProcess.stop();
    }

    public void restartServer(Long id) {
        shutDownServer(id);
        startServer(id);
    }

    public ServerInstanceInfo getServerInstanceInfo(Long id) {
        return processRepository.get(id)
                .map(ServerProcess::getInstanceInfo)
                .orElse(null);
    }

    public boolean isServerInstanceRunning(Server server) {
        return getServerProcess(server).isAlive();
    }

    public void enableAutoRestart(long id, LocalTime time) {
        processRepository.get(id).ifPresent(process -> {
            if (process.isAlive()) {
                process.scheduleRestartJobAt(time);
            }
        });
    }

    public void disableAutoRestart(long id) {
        processRepository.get(id).ifPresent(process -> {
            if (process.isAlive()) {
                process.cancelRestartJob();
            }
        });
    }

    private Server getServer(Long id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Server ID " + id + " not found"));
    }

    private ServerProcess getServerProcess(Server server) {
        return processRepository.get(server.getId())
                .orElseGet(() -> {
                    ServerProcess process = server.getProcess();
                    processRepository.store(server.getId(), process);
                    return process;
                });
    }

    private void validatePortsNotTaken(Server server) {
        serverRepository.findAllByPortOrQueryPort(server.getPort(), server.getQueryPort()).stream()
                .filter(s -> !s.equals(server))
                .forEach(s -> {
                    ServerProcess process = getServerProcess(server);
                    if (process.isAlive()) {
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

    private static void addShutdownHook(ServerProcessRepository processRepository) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> processRepository.getAll()
                .forEach(ServerProcess::stop)));
    }
}
