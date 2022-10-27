package cz.forgottenempire.arma3servergui.steamcmd.entities;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class SteamCmdJob {
    private WorkshopMod relatedWorkshopMod;
    private ServerType relatedServer;
    private ErrorStatus errorStatus;
    @NotNull private SteamCmdParameters steamCmdParameters;

    public SteamCmdJob(ServerType relatedServer, SteamCmdParameters steamCmdParameters) {
        this.relatedServer = relatedServer;
        this.steamCmdParameters = steamCmdParameters;
    }

    public SteamCmdJob(WorkshopMod relatedWorkshopMod, SteamCmdParameters steamCmdParameters) {
        this.relatedWorkshopMod = relatedWorkshopMod;
        this.steamCmdParameters = steamCmdParameters;
    }
}
