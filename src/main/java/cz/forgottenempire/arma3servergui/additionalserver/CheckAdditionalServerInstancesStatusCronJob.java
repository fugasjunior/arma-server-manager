package cz.forgottenempire.arma3servergui.additionalserver;

import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class CheckAdditionalServerInstancesStatusCronJob {

    private final AdditionalServerInstanceInfoRepository instanceInfoRepository;
    private final AdditionalServersService additionalServersService;

    @Autowired
    public CheckAdditionalServerInstancesStatusCronJob(AdditionalServerInstanceInfoRepository instanceInfoRepository,
            AdditionalServersService additionalServersService) {
        this.instanceInfoRepository = instanceInfoRepository;
        this.additionalServersService = additionalServersService;
    }

    @Scheduled(fixedDelay = 1000)
    public void checkServers() {
        instanceInfoRepository.getAll().stream()
                .filter(AdditionalServerInstanceInfo::isAlive)
                .forEach(instance -> {
                    Process process = instance.getProcess();
                    if (process.isAlive()) {
                        // server process is running normally
                        return;
                    }

                    // the process of a server that is marked as alive is no longer running
                    instanceInfoRepository.storeServerInstanceInfo(instance.getId(),
                            new AdditionalServerInstanceInfo(instance.getId(), false, null, null));
                    logWarningMessage(instance);
                });
    }

    private void logWarningMessage(AdditionalServerInstanceInfo instance) {
        AdditionalServer server = additionalServersService.getServer(instance.getId())
                .orElseThrow(() -> new IllegalStateException("Invalid ID " + instance.getId() +
                        " in additional server instances map"));

        log.warn("Server '{}' (ID {}) likely crashed or was exited outside the admin UI. "
                        + "Server was started on {} with process ID {}.",
                server.getName(), server.getId(),
                instance.getStartedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss")),
                instance.getProcess().pid());
    }
}
