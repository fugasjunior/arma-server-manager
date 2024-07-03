package cz.forgottenempire.servermanager.serverinstance;

import com.ibasco.agql.protocols.valve.source.query.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.info.SourceQueryInfoResponse;
import com.ibasco.agql.protocols.valve.source.query.info.SourceServer;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import cz.forgottenempire.servermanager.serverinstance.process.Arma3ServerProcess;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcess;
import cz.forgottenempire.servermanager.serverinstance.process.ServerProcessRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
class CheckServerInstancesStatusCronJob {

    private static final String LOCALHOST = "localhost";

    private final ServerProcessRepository processRepository;
    private final ServerInstanceService serverService;

    @Autowired
    public CheckServerInstancesStatusCronJob(ServerProcessRepository processRepository, ServerInstanceService serverService) {
        this.processRepository = processRepository;
        this.serverService = serverService;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkServers() {
        processRepository.getAll().stream()
                .filter(CheckServerInstancesStatusCronJob::isServerStarted)
                .forEach(this::checkServerProcess);
    }

    private void checkServerProcess(ServerProcess process) {
        if (!process.isAlive()) {
            handleCrashedServer(process);
            return;
        }
        updateHeadlessClients(process);
        updateServerInstanceInfo(process);
    }

    private void handleCrashedServer(ServerProcess serverProcess) {
        serverProcess.stop();
        log.warn("Server ID {} crashed or was exited outside the manager.", serverProcess.getServerId());
    }

    private void updateServerInstanceInfo(ServerProcess process) {
        Server server = getServer(process.getServerId());
        ServerInstanceInfo instanceInfo = process.getInstanceInfo();
        try (SourceQueryClient sourceQueryClient = new SourceQueryClient()) {
            InetSocketAddress serverAddress = new InetSocketAddress(LOCALHOST, server.getQueryPort());
            SourceQueryInfoResponse sourceQueryInfoResponse = sourceQueryClient.getInfo(serverAddress).get(30, TimeUnit.SECONDS);
            SourceServer sourceServer = sourceQueryInfoResponse.getResult();
            updateInstanceInfoFromQueryResult(instanceInfo, sourceServer);
        } catch (ExecutionException e) {
            // ignore any timeouts that happen during the first minute of starting the server
            LocalDateTime startedAt = instanceInfo.getStartedAt();
            if (startedAt.isBefore(LocalDateTime.now().minus(1, ChronoUnit.MINUTES))) {
                log.warn("Timeout happened during querying the status of server {} (ID {}) on port {}. " +
                                "It may not have finished initialization yet. If this message keeps occurring, " +
                                "there's likely a problem with the server.",
                        server.getName(), server.getId(), server.getQueryPort());
            }
        } catch (Exception e) {
            log.error("Couldn't query server {} (ID {}) on port {}",
                    server.getName(), server.getId(), server.getQueryPort(), e);
        }
    }

    private static void updateInstanceInfoFromQueryResult(ServerInstanceInfo instanceInfo, SourceServer queryServerInfo) {
        instanceInfo.setMap(queryServerInfo.getMapName());
        instanceInfo.setPlayersOnline(queryServerInfo.getNumOfPlayers());
        instanceInfo.setMaxPlayers(queryServerInfo.getMaxPlayers());
        instanceInfo.setVersion(queryServerInfo.getGameVersion());
        instanceInfo.setDescription(queryServerInfo.getGameDescription());
    }

    private Server getServer(Long serverId) {
        return serverService.getServer(serverId)
                .orElseThrow(() -> new IllegalStateException("Invalid ID " + serverId + " in server instances map"));
    }

    private static boolean isServerStarted(ServerProcess process) {
        return process.getInstanceInfo() != null && process.getInstanceInfo().isAlive();
    }

    private static void updateHeadlessClients(ServerProcess process) {
        if (process instanceof Arma3ServerProcess arma3ServerProcess) {
            arma3ServerProcess.checkHeadlessClients();
        }
    }
}
