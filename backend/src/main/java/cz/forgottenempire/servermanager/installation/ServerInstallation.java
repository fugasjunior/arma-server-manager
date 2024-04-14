package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import java.time.LocalDateTime;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
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
