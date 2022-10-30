package cz.forgottenempire.arma3servergui.steamcmd;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import java.util.concurrent.CompletableFuture;

public interface SteamCmdService {

    CompletableFuture<SteamCmdJob> installOrUpdateServer(ServerType serverType);

    CompletableFuture<SteamCmdJob> installOrUpdateWorkshopMod(WorkshopMod workshopMod);
}
