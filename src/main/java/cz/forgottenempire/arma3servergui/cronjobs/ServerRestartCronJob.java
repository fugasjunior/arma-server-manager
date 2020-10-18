package cz.forgottenempire.arma3servergui.cronjobs;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.SettingsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerRestartCronJob {

    private ArmaServerService serverService;
    private SettingsService settingsService;

    public ServerRestartCronJob() {
        log.info("Scheduling server restart cronjob for 05:00 AM every day");
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void refreshMods() {
        log.info("Running server restart job");

        if (serverService.isServerProcessAlive()) {
            log.info("Restarting server...");
            ServerSettings settings = settingsService.getServerSettings();
            if (serverService.restartServer(settings)) {
                log.info("Server restarted");
            } else {
                log.error("Error during server restart!");
            }
        } else {
            log.info("Server is not up, no restart needed");
        }
    }

    @Autowired
    public void setServerService(ArmaServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
