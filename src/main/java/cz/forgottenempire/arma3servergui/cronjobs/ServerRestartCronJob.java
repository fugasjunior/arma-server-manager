package cz.forgottenempire.arma3servergui.cronjobs;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ServerRestartCronJob {
    ArmaServerService serverService;
    JsonDbService<ServerSettings> settingsDb;

    public ServerRestartCronJob() {
        log.info("Scheduling server restart cronjob for 05:00 AM every day");
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void refreshMods() {
        log.info("Running server restart job");

        if (serverService.isServerProcessAlive()) {
            log.info("Restarting server...");
            ServerSettings settings = settingsDb.find(Constants.SERVER_MAIN_ID, ServerSettings.class);
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
    public void setSettingsDb(JsonDbService<ServerSettings> settingsDb) {
        this.settingsDb = settingsDb;
    }
}
