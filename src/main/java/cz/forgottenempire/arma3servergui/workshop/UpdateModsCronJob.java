package cz.forgottenempire.arma3servergui.workshop;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
class UpdateModsCronJob {

    private final WorkshopModsFacade modsFacade;

    @Autowired
    public UpdateModsCronJob(WorkshopModsFacade modsFacade) {
        // TODO make customizable through UI
        log.info("Scheduling mod update cronjob for 03:00 AM every day");
        this.modsFacade = modsFacade;
    }

    @Scheduled(cron = "0 0 3 * * *")
    public void refreshMods() {
        log.info("Running update job");
        modsFacade.updateAllMods();
    }
}
