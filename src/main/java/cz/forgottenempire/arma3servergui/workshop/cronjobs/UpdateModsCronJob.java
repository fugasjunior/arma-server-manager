package cz.forgottenempire.arma3servergui.workshop.cronjobs;

import cz.forgottenempire.arma3servergui.workshop.services.WorkshopModsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UpdateModsCronJob {

    private WorkshopModsService modsService;

    public UpdateModsCronJob() {
        log.info("Scheduling mod update cronjob for 03:00 AM every day");
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void refreshMods() {
        log.info("Running update job");
        modsService.updateAllMods();
    }

    @Autowired
    public void setModsService(WorkshopModsService modsService) {
        this.modsService = modsService;
    }
}
