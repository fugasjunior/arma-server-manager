package cz.forgottenempire.servermanager.serverinstance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Setter
@AllArgsConstructor
@Builder
public
class ServerInstanceInfo {

    private LocalDateTime startedAt;
    private int playersOnline;
    private int maxPlayers;
    private String version;
    private String map;
    private String description;
    private int headlessClientsCount;

    public boolean isAlive() {
        return startedAt != null;
    }
}
