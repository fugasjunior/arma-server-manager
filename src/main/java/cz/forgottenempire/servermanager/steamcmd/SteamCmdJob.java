package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
public class SteamCmdJob {

    private Collection<WorkshopMod> relatedWorkshopMods;
    private ServerType relatedServer;
    private ErrorStatus errorStatus;
    @NotNull
    private SteamCmdParameters steamCmdParameters;

    public SteamCmdJob(ServerType relatedServer, SteamCmdParameters steamCmdParameters) {
        this.relatedServer = relatedServer;
        this.steamCmdParameters = steamCmdParameters;
    }

    public SteamCmdJob(Collection<WorkshopMod> relatedWorkshopMods, SteamCmdParameters steamCmdParameters) {
        this.relatedWorkshopMods = relatedWorkshopMods;
        this.steamCmdParameters = steamCmdParameters;
    }
}
