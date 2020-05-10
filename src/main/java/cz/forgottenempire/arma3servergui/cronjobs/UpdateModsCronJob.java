package cz.forgottenempire.arma3servergui.cronjobs;

import cz.forgottenempire.arma3servergui.services.SteamCmdService;
import cz.forgottenempire.arma3servergui.services.SteamCredentialsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateModsCronJob {
    private SteamCmdService steamCmdService;
    private SteamCredentialsService credentialsService;

    public UpdateModsCronJob() {
        log.info("Scheduling mod update cronjob for 02:00 AM every day");
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void refreshMods() {
        log.info("Running update job");
        steamCmdService.refreshMods(credentialsService.getAuthAccount());
        log.info("Update job finished");
    }

    @Autowired
    public void setSteamCmdService(SteamCmdService steamCmdService) {
        this.steamCmdService = steamCmdService;
    }

    @Autowired
    public void setCredentialsService(SteamCredentialsService credentialsService) {
        this.credentialsService = credentialsService;
    }
}
