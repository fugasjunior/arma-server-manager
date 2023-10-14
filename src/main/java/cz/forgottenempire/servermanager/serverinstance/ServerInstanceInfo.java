package cz.forgottenempire.servermanager.serverinstance;

import java.time.LocalDateTime;
import javax.annotation.concurrent.Immutable;

import lombok.*;

@Data
@Setter
@AllArgsConstructor
@Builder
class ServerInstanceInfo {

    private final long id;
    private LocalDateTime startedAt;
    private Process process;
    private int playersOnline;
    private int maxPlayers;
    private String version;
    private String map;
    private String description;

    public boolean isAlive() {
        return startedAt != null;
    }
}
