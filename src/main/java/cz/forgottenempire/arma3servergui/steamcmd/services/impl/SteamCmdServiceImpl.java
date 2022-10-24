package cz.forgottenempire.arma3servergui.steamcmd.services.impl;

import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.server.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.SteamCmdExecutor;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdParameters;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdParameters.Builder;
import cz.forgottenempire.arma3servergui.steamcmd.repositories.SteamCmdJobRepository;
import cz.forgottenempire.arma3servergui.steamcmd.services.SteamCmdService;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.concurrent.CompletableFuture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SteamCmdServiceImpl implements SteamCmdService {

    @Value("${installDir}")
    private String ARMA3_MODS_DOWNLOAD_PATH;

    @Value("${serverDir}")
    private String ARMA3_SERVER_DIR;

    private final SteamCmdJobRepository steamCmdJobRepository;

    private final SteamCmdExecutor steamCmdExecutor;

    @Autowired
    public SteamCmdServiceImpl(SteamCmdJobRepository steamCmdJobRepository, SteamCmdExecutor steamCmdExecutor) {
        this.steamCmdJobRepository = steamCmdJobRepository;
        this.steamCmdExecutor = steamCmdExecutor;
    }

    @Override
    public CompletableFuture<SteamCmdJob> installOrUpdateServer(ServerType serverType) {
        SteamCmdParameters parameters = new Builder()
                .withLogin()
                .withInstallDir(ARMA3_SERVER_DIR)
                .withAppInstall(Constants.STEAM_ARMA3SERVER_ID, true)
                .build();
        return saveAndEnqueueJob(new SteamCmdJob(serverType, parameters));
    }

    @Override
    public CompletableFuture<SteamCmdJob> installOrUpdateWorkshopMod(WorkshopMod workshopMod) {
        SteamCmdParameters parameters = new Builder()
                .withLogin()
                .withInstallDir(ARMA3_MODS_DOWNLOAD_PATH)
                .withWorkshopItemInstall(Constants.STEAM_ARMA3_ID, workshopMod.getId(), true)
                .build();
        return saveAndEnqueueJob(new SteamCmdJob(workshopMod, parameters));
    }

    private CompletableFuture<SteamCmdJob> saveAndEnqueueJob(SteamCmdJob job) {
        CompletableFuture<SteamCmdJob> future = new CompletableFuture<>();
        steamCmdJobRepository.save(job);
        steamCmdExecutor.processJob(job, future);
        return future;
    }
}
