package cz.forgottenempire.arma3servergui.cronjobs;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import cz.forgottenempire.arma3servergui.services.SteamCmdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateModsCronJob {
    private SteamCmdService steamCmdService;
    private JsonDbService<SteamAuth> steamAuthDb;

    public UpdateModsCronJob() {
        log.info("Scheduling mod update cronjob for 02:00 AM every day");
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void refreshMods() {
        log.info("Running update job");
        SteamAuth auth = steamAuthDb.find(Constants.ACCOUND_DEFAULT_ID, SteamAuth.class);
        if (auth != null && auth.getUsername() != null && auth.getPassword() != null) {
            steamCmdService.refreshMods(auth);
            log.info("Update job finished");
        } else {
            log.warn("Could not finish update job, no auth for steam workshop given");
        }
    }

    @Autowired
    public void setSteamCmdService(SteamCmdService steamCmdService) {
        this.steamCmdService = steamCmdService;
    }

    @Autowired
    public void setSteamAuthDb(JsonDbService<SteamAuth> steamAuthDb) {
        this.steamAuthDb = steamAuthDb;
    }
}
