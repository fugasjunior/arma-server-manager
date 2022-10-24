package cz.forgottenempire.arma3servergui.steamcmd;

import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.repositories.SteamCmdJobRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SteamCmdJobCleanupCronJob {

    private final SteamCmdJobRepository steamCmdJobRepository;

    @Autowired
    public SteamCmdJobCleanupCronJob(SteamCmdJobRepository steamCmdJobRepository) {
        this.steamCmdJobRepository = steamCmdJobRepository;
    }

    @Scheduled(cron = "0 0 * * *")
    public void cleanupOldSteamCmdJobStatuses() {
        log.info("Cleaning up SteamCMD job statuses older than 1 month");
        List<SteamCmdJob> jobStatusesToDelete = steamCmdJobRepository.findAllByCreatedAtBefore(
                LocalDateTime.now().minusMonths(1));
        int jobsCount = jobStatusesToDelete.size();
        steamCmdJobRepository.deleteAll(jobStatusesToDelete);
        log.info("Cleanup job finished. {} entries cleaned up", jobsCount);
    }
}
