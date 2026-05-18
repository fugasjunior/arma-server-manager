package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.serverinstance.AutomaticRestartScheduler;
import cz.forgottenempire.servermanager.serverinstance.ServerInstanceInfo;
import cz.forgottenempire.servermanager.serverinstance.ServerRepository;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.exceptions.PortAlreadyTakenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;

@Service
@Slf4j
public class ServerProcessService {

    private final ServerRepository serverRepository;
    private final ServerProcessRepository processRepository;
    private final ServerProcessFactory processFactory;
    private final AutomaticRestartScheduler restartScheduler;

    @Autowired
    public ServerProcessService(
            ServerRepository serverRepository,
            ServerProcessRepository processRepository,
            ServerProcessFactory processFactory,
            AutomaticRestartScheduler restartScheduler
    ) {
        this.serverRepository = serverRepository;
        this.processRepository = processRepository;
        this.processFactory = processFactory;
        this.restartScheduler = restartScheduler;
        addShutdownHook(processRepository);
    }

    public void startServer(Long id) {
        Server server = getServer(id);
        ServerProcess serverProcess = getOrCreateServerProcess(server);

        if (serverProcess.isAlive()) {
            return;
        }

        validatePortsNotTaken(server);
        serverProcess.start(server);

        if (serverProcess.isAlive() && server instanceof Arma3Server arma3Server && serverProcess instanceof Arma3ServerProcess arma3Process) {
            arma3Process.reconcileHeadlessClients(arma3Server);
        }

        if (server.isRestartAutomatically()) {
            restartScheduler.schedule(id, server.getAutomaticRestartTime(), () -> restartServer(id));
        }
    }

    public void shutDownServer(Long id) {
        ServerProcess serverProcess = processRepository.get(id)
                .orElse(null);
        if (serverProcess != null) {
            serverProcess.stop();
        }
        restartScheduler.cancel(id);
    }

    public void restartServer(Long id) {
        Server server = getServer(id);
        ServerProcess serverProcess = getOrCreateServerProcess(server);
        serverProcess.restart(server);

        if (server.isRestartAutomatically()) {
            restartScheduler.schedule(id, server.getAutomaticRestartTime(), () -> restartServer(id));
        }
    }

    public ServerInstanceInfo getServerInstanceInfo(Long id) {
        return processRepository.get(id)
                .map(ServerProcess::getInstanceInfo)
                .orElse(null);
    }

    public boolean isServerInstanceRunning(Server server) {
        return processRepository.get(server.getId())
                .map(ServerProcess::isAlive)
                .orElse(false);
    }

    public void enableAutoRestart(long id, LocalTime time) {
        processRepository.get(id).ifPresent(process -> {
            if (process.isAlive()) {
                restartScheduler.schedule(id, time, () -> restartServer(id));
            }
        });
    }

    public void disableAutoRestart(long id) {
        restartScheduler.cancel(id);
    }

    private Server getServer(Long id) {
        return serverRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Server ID " + id + " not found"));
    }

    private ServerProcess getOrCreateServerProcess(Server server) {
        return processRepository.get(server.getId())
                .orElseGet(() -> {
                    ServerProcess process = processFactory.create(server);
                    processRepository.store(server.getId(), process);
                    return process;
                });
    }

    private void validatePortsNotTaken(Server server) {
        serverRepository.findAllByPortOrQueryPort(server.getPort(), server.getQueryPort()).stream()
                .filter(s -> !s.equals(server))
                .forEach(s -> {
                    ServerProcess process = getOrCreateServerProcess(s);
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
