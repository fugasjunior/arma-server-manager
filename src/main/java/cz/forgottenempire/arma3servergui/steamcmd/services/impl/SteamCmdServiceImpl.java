package cz.forgottenempire.arma3servergui.steamcmd.services.impl;

import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.common.services.PathsFactory;
import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.SteamCmdExecutor;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdParameters;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdParameters.Builder;
import cz.forgottenempire.arma3servergui.steamcmd.services.SteamCmdService;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import javax.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

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
                .withLogin()
                .withInstallDir(pathsFactory.getServerPath(serverType).toAbsolutePath().toString())
                .withAppInstall(Constants.SERVER_IDS.get(serverType), true, betaBranchParameter)
                .build();
        return enqueueJob(new SteamCmdJob(serverType, parameters));
    }

    @Override
    public CompletableFuture<SteamCmdJob> installOrUpdateWorkshopMod(WorkshopMod workshopMod) {
        SteamCmdParameters parameters = new Builder()
                .withLogin()
                .withInstallDir(pathsFactory.getModsBasePath().toAbsolutePath().toString())
                .withWorkshopItemInstall(Constants.GAME_IDS.get(ServerType.ARMA3), workshopMod.getId(), true)
                .build();
        return enqueueJob(new SteamCmdJob(workshopMod, parameters));
    }

    private CompletableFuture<SteamCmdJob> enqueueJob(SteamCmdJob job) {
        CompletableFuture<SteamCmdJob> future = new CompletableFuture<>();
        steamCmdExecutor.processJob(job, future);
        return future;
    }
}
