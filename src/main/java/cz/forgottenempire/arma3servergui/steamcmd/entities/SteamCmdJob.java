package cz.forgottenempire.arma3servergui.steamcmd.entities;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
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
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class SteamCmdJob {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    private WorkshopMod relatedWorkshopMod;
    private ServerType relatedServer;

    @NotNull
    @Enumerated(EnumType.STRING)
    private JobStatus state;

    @Enumerated(EnumType.STRING)
    private ErrorStatus errorStatus;

    @Embedded
    private SteamCmdParameters steamCmdParameters;

    public SteamCmdJob() {
        createdAt = LocalDateTime.now();
        state = JobStatus.QUEUED;
    }

    public SteamCmdJob(ServerType relatedServer, SteamCmdParameters steamCmdParameters) {
        this.relatedServer = relatedServer;
        this.steamCmdParameters = steamCmdParameters;
    }

    public SteamCmdJob(WorkshopMod relatedWorkshopMod, SteamCmdParameters steamCmdParameters) {
        this.relatedWorkshopMod = relatedWorkshopMod;
        this.steamCmdParameters = steamCmdParameters;
    }


    public enum JobStatus {
        QUEUED,
        RUNNING,
        FINISHED,
        INTERRUPTED,
        FAILED
    }
}
