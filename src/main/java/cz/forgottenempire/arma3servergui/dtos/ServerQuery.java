package cz.forgottenempire.arma3servergui.dtos;

import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerQuery {
    private boolean isServerUp;
    private int playersOnline;
    private int maxPlayers;
    private String gameVersion;
    private String mapName;
    private String gameDescription;

    public static ServerQuery from(SourceServer sourceServer) {
        ServerQuery status = new ServerQuery();

        if (sourceServer == null) {
            status.isServerUp = false;
            status.playersOnline = 0;
            return status;
        }

        status.setServerUp(true);
        status.setPlayersOnline(sourceServer.getNumOfPlayers());
        status.setMaxPlayers(sourceServer.getMaxPlayers());
        status.setMapName(sourceServer.getMapName());
        status.setGameVersion(sourceServer.getGameVersion());
        status.setGameDescription(sourceServer.getGameDescription());
        return status;
    }
}
