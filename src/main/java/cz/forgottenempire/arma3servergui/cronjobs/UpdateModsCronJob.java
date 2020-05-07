package cz.forgottenempire.arma3servergui.cronjobs;

import cz.forgottenempire.arma3servergui.services.SteamCmdService;
import cz.forgottenempire.arma3servergui.services.SteamCredentialsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class UpdateModsCronJob {
    private final Logger logger = LoggerFactory.getLogger(UpdateModsCronJob.class);

    private SteamCmdService steamCmdService;
    private SteamCredentialsService credentialsService;

    public UpdateModsCronJob() {
        logger.info("Scheduling mod update cronjob for 02:00 AM every day");
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void refreshMods() {
        logger.info("Running update job");
        steamCmdService.refreshMods(credentialsService.getAuthAccount());
        logger.info("Update job finished");
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
