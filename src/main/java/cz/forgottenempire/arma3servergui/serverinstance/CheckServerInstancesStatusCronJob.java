package cz.forgottenempire.arma3servergui.serverinstance;

import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import cz.forgottenempire.arma3servergui.serverinstance.entities.Server;
import java.net.InetSocketAddress;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class CheckServerInstancesStatusCronJob {

    private static final String LOCALHOST = "localhost";

    private final ServerInstanceInfoRepository instanceInfoRepository;

    private final ServerInstanceService serverService;


    @Autowired
    public CheckServerInstancesStatusCronJob(ServerInstanceInfoRepository instanceInfoRepository,
            ServerInstanceService serverService) {
        this.instanceInfoRepository = instanceInfoRepository;
        this.serverService = serverService;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkServers() {
        instanceInfoRepository.getAll().stream()
                .filter(ServerInstanceInfo::isAlive)
                .forEach(server -> {
                    if (checkCrashed(server)) {
                        handleCrashedServer(server);
                        return;
                    }
                    updateServerInstanceInfo(server);
                });
    }

    private boolean checkCrashed(ServerInstanceInfo instanceInfo) {
        Process process = instanceInfo.getProcess();
        return process == null || !process.isAlive();
    }

    private void handleCrashedServer(ServerInstanceInfo instanceInfo) {
        Server server = getServer(instanceInfo.getId());

        instanceInfoRepository.storeServerInstanceInfo(instanceInfo.getId(), ServerInstanceInfo.builder()
                .id(instanceInfo.getId()).alive(false).build());

        log.warn("Server '{}' (ID {}, type '{}') likely crashed or was exited outside the admin UI. "
                        + "Server was started on {} with process ID {}.",
                server.getName(), server.getId(), server.getType(),
                instanceInfo.getStartedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")),
                instanceInfo.getProcess().pid());
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
                    .alive(true)
                    .playersOnline(queryServerInfo.getNumOfPlayers())
                    .maxPlayers(server.getMaxPlayers())
                    .map(queryServerInfo.getMapName())
                    .version(queryServerInfo.getGameVersion())
                    .description(queryServerInfo.getGameDescription())
                    .build());

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
