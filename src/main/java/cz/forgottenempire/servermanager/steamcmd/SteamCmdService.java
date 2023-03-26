package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Service
public class SteamCmdService {

    private final SteamCmdExecutor steamCmdExecutor;
    private final PathsFactory pathsFactory;

    @Autowired
    public SteamCmdService(SteamCmdExecutor steamCmdExecutor, PathsFactory pathsFactory) {
        this.steamCmdExecutor = steamCmdExecutor;
        this.pathsFactory = pathsFactory;
    }

    public CompletableFuture<SteamCmdJob> installOrUpdateServer(ServerType serverType) {
        String betaBranchParameter = serverType == ServerType.ARMA3 ? "-beta creatordlc" : null;

        SteamCmdParameters parameters = new SteamCmdParameters.Builder()
                .withInstallDir(pathsFactory.getServerPath(serverType).toAbsolutePath().toString())
                .withLogin()
                .withAppInstall(Constants.SERVER_IDS.get(serverType), true, betaBranchParameter)
                .build();
        return enqueueJob(new SteamCmdJob(serverType, parameters));
    }

    public CompletableFuture<SteamCmdJob> installOrUpdateWorkshopMods(Collection<WorkshopMod> workshopMods) {
        SteamCmdParameters.Builder parameters = new SteamCmdParameters.Builder()
                .withInstallDir(pathsFactory.getModsBasePath().toAbsolutePath().toString())
                .withLogin();

        workshopMods.forEach(mod ->
                parameters.withWorkshopItemInstall(
                        Constants.GAME_IDS.get(mod.getServerType()),
                        mod.getId(), true
                )
        );

        return enqueueJob(new SteamCmdJob(workshopMods, parameters.build()));
    }

    private CompletableFuture<SteamCmdJob> enqueueJob(SteamCmdJob job) {
        CompletableFuture<SteamCmdJob> future = new CompletableFuture<>();
        steamCmdExecutor.processJob(job, future);
        return future;
    }
}
