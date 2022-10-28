package cz.forgottenempire.arma3servergui.steamcmd;

import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.common.PathsFactory;
import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.SteamCmdParameters.Builder;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SteamCmdServiceImpl implements SteamCmdService {

    private final SteamCmdExecutor steamCmdExecutor;
    private final PathsFactory pathsFactory;

    @Autowired
    public SteamCmdServiceImpl(SteamCmdExecutor steamCmdExecutor, PathsFactory pathsFactory) {
        this.steamCmdExecutor = steamCmdExecutor;
        this.pathsFactory = pathsFactory;
    }

    @Override
    public CompletableFuture<SteamCmdJob> installOrUpdateServer(ServerType serverType) {
        String betaBranchParameter = serverType == ServerType.ARMA3 ? "-beta creatordlc" : null;

        SteamCmdParameters parameters = new Builder()
                .withInstallDir(pathsFactory.getServerPath(serverType).toAbsolutePath().toString())
                .withLogin()
                .withAppInstall(Constants.SERVER_IDS.get(serverType), true, betaBranchParameter)
                .build();
        return enqueueJob(new SteamCmdJob(serverType, parameters));
    }

    @Override
    public CompletableFuture<SteamCmdJob> installOrUpdateWorkshopMod(WorkshopMod workshopMod) {
        SteamCmdParameters parameters = new Builder()
                .withInstallDir(pathsFactory.getModsBasePath().toAbsolutePath().toString())
                .withLogin()
                .withWorkshopItemInstall(
                        Constants.GAME_IDS.get(workshopMod.getServerType()),
                        workshopMod.getId(), true)
                .build();
        return enqueueJob(new SteamCmdJob(workshopMod, parameters));
    }

    private CompletableFuture<SteamCmdJob> enqueueJob(SteamCmdJob job) {
        CompletableFuture<SteamCmdJob> future = new CompletableFuture<>();
        steamCmdExecutor.processJob(job, future);
        return future;
    }
}
