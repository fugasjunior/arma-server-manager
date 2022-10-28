package cz.forgottenempire.arma3servergui.installation;

import cz.forgottenempire.arma3servergui.common.InstallationStatus;
import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
class ServerInstallation {

    @Id
    @Enumerated(EnumType.STRING)
    private ServerType type;

    private String version;
    private LocalDateTime lastUpdatedAt;

    @Enumerated(EnumType.STRING)
    private InstallationStatus installationStatus;

    @Enumerated(EnumType.STRING)
    private ErrorStatus errorStatus;

    public ServerInstallation(ServerType type) {
        this.type = type;
    }
}
