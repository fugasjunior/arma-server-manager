package cz.forgottenempire.arma3servergui.steamcmd.entities;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.time.LocalDateTime;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class SteamCmdJob {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private WorkshopMod relatedWorkshopMod;
    private ServerType relatedServer;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Embedded
    private SteamCmdParameters steamCmdParameters;

    public enum JobStatus {
        QUEUED,
        RUNNING,
        FINISHED,
        INTERRUPTED,
        FAILED
    }
}
