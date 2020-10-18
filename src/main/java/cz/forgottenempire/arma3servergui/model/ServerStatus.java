package cz.forgottenempire.arma3servergui.model;

import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerStatus {

    private boolean isServerUp;
    private int playersOnline;
    private int maxPlayers;
    private String gameVersion;
    private String mapName;
    private String gameDescription;

    public synchronized void setFromSourceServer(SourceServer sourceServer) {
        if (sourceServer == null) {
            resetStatus();
            return;
        }

        isServerUp = true;
        playersOnline = sourceServer.getNumOfPlayers();
        maxPlayers = sourceServer.getMaxPlayers();
        mapName = sourceServer.getMapName();
        gameVersion = sourceServer.getGameVersion();
        gameDescription = sourceServer.getGameDescription();
    }

    public void resetStatus() {
        isServerUp = false;
        playersOnline = 0;
        maxPlayers = 0;
        mapName = "";
        gameVersion = "";
        gameDescription = "";
    }
}