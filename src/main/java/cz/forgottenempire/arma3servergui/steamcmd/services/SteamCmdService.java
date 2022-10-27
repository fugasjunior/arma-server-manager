package cz.forgottenempire.arma3servergui.steamcmd.services;

import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;

public interface SteamCmdService {

    CompletableFuture<SteamCmdJob> installOrUpdateServer(ServerType serverType);

    CompletableFuture<SteamCmdJob> installOrUpdateWorkshopMod(WorkshopMod workshopMod);
}
