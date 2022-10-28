package cz.forgottenempire.arma3servergui.serverinstance;

import java.time.LocalDateTime;
import javax.annotation.concurrent.Immutable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Immutable
@AllArgsConstructor
@Builder
class ServerInstanceInfo {

    private final long id;
    private final boolean alive;
    private final LocalDateTime startedAt;
    private final Process process;
    private final int playersOnline;
    private final int maxPlayers;
    private final String version;
    private final String map;
    private final String description;
}
