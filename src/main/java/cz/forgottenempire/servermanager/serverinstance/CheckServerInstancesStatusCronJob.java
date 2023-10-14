package cz.forgottenempire.servermanager.serverinstance;

import com.ibasco.agql.core.exceptions.ReadTimeoutException;
import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
class CheckServerInstancesStatusCronJob {

    private static final String LOCALHOST = "localhost";

    private final ServerInstanceInfoRepository instanceInfoRepository;
    private final ServerProcessRepository processRepository;

    private final ServerInstanceService serverService;


    @Autowired
    public CheckServerInstancesStatusCronJob(ServerInstanceInfoRepository instanceInfoRepository,
                                             ServerProcessRepository processRepository, ServerInstanceService serverService) {
        this.instanceInfoRepository = instanceInfoRepository;
        this.processRepository = processRepository;
        this.serverService = serverService;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkServers() {
        processRepository.getAll().stream()
                .filter(process -> process.getInstanceInfo().isAlive())
                .forEach(process -> {
                    if (checkCrashed(process)) {
                        handleCrashedServer(process);
                        return;
                    }
                    updateServerInstanceInfo(process.getInstanceInfo());
                });
    }

    private boolean checkCrashed(ServerProcess serverProcess) {
        boolean hasBeenStarted = serverProcess.getInstanceInfo().isAlive();
        return hasBeenStarted && !serverProcess.isAlive();
    }

    private void handleCrashedServer(ServerProcess serverProcess) {
        ServerInstanceInfo instanceInfo = serverProcess.getInstanceInfo();
        instanceInfo.setStartedAt(null);
        instanceInfo.setProcess(null);
        instanceInfo.setPlayersOnline(0);
        instanceInfo.setMaxPlayers(0);
        instanceInfo.setVersion(null);
        instanceInfo.setMap(null);
        instanceInfo.setDescription(null);
        // TODO fix log
//        log.warn("Server '{}' (ID {}, type '{}') likely crashed or was exited outside the admin UI. "
//                        + "Server was started on {} with process ID {}.",
//                server.getName(), sserver.getId(), server.getType(),
//                instanceInfo.getStartedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")),
//                instanceInfo.getProcess().pid());
    }

    private void updateServerInstanceInfo(ServerInstanceInfo instanceInfo) {
        Server server = getServer(instanceInfo.getId());
        try (SourceQueryClient sourceQueryClient = new SourceQueryClient()) {
            InetSocketAddress serverAddress = new InetSocketAddress(LOCALHOST, server.getQueryPort());
            SourceServer queryServerInfo = sourceQueryClient.getServerInfo(serverAddress).get(30, TimeUnit.SECONDS);

            instanceInfoRepository.storeServerInstanceInfo(instanceInfo.getId(), ServerInstanceInfo.builder()
                    .id(instanceInfo.getId())
                    .process(instanceInfo.getProcess())
                    .startedAt(instanceInfo.getStartedAt())
                    .playersOnline(queryServerInfo.getNumOfPlayers())
                    .maxPlayers(server.getMaxPlayers())
                    .map(queryServerInfo.getMapName())
                    .version(queryServerInfo.getGameVersion())
                    .description(queryServerInfo.getGameDescription())
                    .build());
        } catch (ReadTimeoutException e) {
            // ignore any timeouts that happen during the first minute of starting the server
            LocalDateTime startedAt = instanceInfo.getStartedAt();
            if (startedAt.isBefore(LocalDateTime.now().minus(1, ChronoUnit.MINUTES))) {
                log.warn("Timeout happened during querying the status of server {} (ID {}) on port {}. " +
                                "It may not have finished initialization yet. If this message keeps occurring, " +
                                "there's likely a problem with the server.",
                        server.getName(), instanceInfo.getId(), server.getQueryPort());
            }
        } catch (Exception e) {
            log.error("Couldn't query server {} (ID {}) on port {}",
                    server.getName(), instanceInfo.getId(), server.getQueryPort(), e);
        }
    }

    private Server getServer(Long serverId) {
        return serverService.getServer(serverId)
                .orElseThrow(() -> new IllegalStateException("Invalid ID " + serverId + " in server instances map"));
    }
}
