package cz.forgottenempire.arma3servergui.cronjobs;

import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.ServerStatus;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Component
@Slf4j
public class QueryServerStatusCronJob {

    @Value("${hostName}")
    private String hostName;

    private JsonDbService<ServerSettings> settingsDb;
    private ArmaServerService serverService;
    private ServerStatus serverStatus;

    @Scheduled(fixedDelay = 10 * 1000)
    public void queryServer() {
        if (!serverService.isServerProcessAlive()) {
            serverStatus.resetStatus();
        } else {
            ServerSettings settings = settingsDb.find(Constants.SERVER_MAIN_ID, ServerSettings.class);
            SourceServer serverInfo = null;
            try (SourceQueryClient sourceQueryClient = new SourceQueryClient()) {
                // default steam query port is (game server port + 1)
                InetSocketAddress serverAddress = new InetSocketAddress(hostName, settings.getPort() + 1);
                serverInfo = sourceQueryClient.getServerInfo(serverAddress).get();
            } catch (Exception ignored) {}

            serverStatus.setFromSourceServer(serverInfo);
        }
    }

    @Autowired
    public void setServerService(ArmaServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setServerStatus(ServerStatus serverStatus) { this.serverStatus = serverStatus; }

    @Autowired
    public void setSettingsDb(JsonDbService<ServerSettings> settingsDb) {
        this.settingsDb = settingsDb;
    }
}
