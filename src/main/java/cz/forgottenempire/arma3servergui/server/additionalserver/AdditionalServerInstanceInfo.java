package cz.forgottenempire.arma3servergui.server.additionalserver;

import java.time.LocalDateTime;
import javax.annotation.concurrent.Immutable;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Immutable
@AllArgsConstructor
public class AdditionalServerInstanceInfo {
    private final long id;
    private final boolean alive;
    private final LocalDateTime startedAt;
    private final Process process;
}
