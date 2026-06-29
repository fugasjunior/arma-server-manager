package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.installation.ServerInstallation;
import cz.forgottenempire.servermanager.steamauth.SteamAuthService;
import cz.forgottenempire.servermanager.steamauth.SteamLoginService;
import cz.forgottenempire.servermanager.steamauth.SteamLoginServiceResult;
import cz.forgottenempire.servermanager.api.model.SteamLoginResult;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Service
public class SteamCmdService {

    private final SteamCmdExecutor steamCmdExecutor;
    private final PathsFactory pathsFactory;
    private final SteamAuthService steamAuthService;
    private final SteamLoginService steamLoginService;

    @Autowired
    SteamCmdService(
            SteamCmdExecutor steamCmdExecutor,
            PathsFactory pathsFactory,
            SteamAuthService steamAuthService,
            SteamLoginService steamLoginService
    ) {
        this.steamCmdExecutor = steamCmdExecutor;
        this.pathsFactory = pathsFactory;
        this.steamAuthService = steamAuthService;
        this.steamLoginService = steamLoginService;
    }

    public boolean isBusy() {
        return steamCmdExecutor.isBusy();
    }

    public CompletableFuture<SteamCmdJob> installOrUpdateServer(ServerInstallation server) {
        SteamCmdJob errorJob = getErrorJobIfUnavailable(new SteamCmdJob(server.getType(), null));
        if (errorJob != null) {
            return CompletableFuture.completedFuture(errorJob);
        }

        String username = steamAuthService.getAuthAccount().getUsername();
        ServerType serverType = server.getType();
        String betaBranchParameter = "-beta " + server.getBranch().toString().toLowerCase();

        SteamCmdParameters parameters = new SteamCmdParameters.Builder()
                .withInstallDir(pathsFactory.getServerPath(serverType).toAbsolutePath().toString())
                .withCachedLogin(username)
                .withAppInstall(Constants.SERVER_IDS.get(serverType), true, betaBranchParameter)
                .build();
        return enqueueJob(new SteamCmdJob(serverType, parameters));
    }

    public CompletableFuture<SteamCmdJob> installOrUpdateWorkshopMods(Collection<WorkshopMod> workshopMods) {
        SteamCmdJob errorJob = getErrorJobIfUnavailable(new SteamCmdJob(workshopMods, null));
        if (errorJob != null) {
            return CompletableFuture.completedFuture(errorJob);
        }

        String username = steamAuthService.getAuthAccount().getUsername();
        SteamCmdParameters.Builder parameters = new SteamCmdParameters.Builder()
                .continueOnFailedCommand()
                .withInstallDir(pathsFactory.getModsBasePath().toAbsolutePath().toString())
                .withCachedLogin(username);

        workshopMods.forEach(mod ->
                parameters.withWorkshopItemInstall(
                        Constants.GAME_IDS.get(mod.getServerType()),
                        mod.getId(), true
                )
        );

        return enqueueJob(new SteamCmdJob(workshopMods, parameters.build()));
    }

    /**
     * Returns a pre-failed job if auth is not configured or a login is in progress, otherwise null.
     */
    private SteamCmdJob getErrorJobIfUnavailable(SteamCmdJob template) {
        if (steamLoginService.isLoginInProgress()) {
            template.setErrorStatus(ErrorStatus.GENERIC);
            return template;
        }
        if (steamAuthService.getAuthAccount().getUsername() == null) {
            template.setErrorStatus(ErrorStatus.WRONG_AUTH);
            return template;
        }
        return null;
    }

    private CompletableFuture<SteamCmdJob> enqueueJob(SteamCmdJob job) {
        return enqueueJob(job, true);
    }

    private CompletableFuture<SteamCmdJob> enqueueJob(SteamCmdJob job, boolean retryOnReauth) {
        CompletableFuture<SteamCmdJob> future = new CompletableFuture<>();
        steamCmdExecutor.processJob(job, future);

        return future.thenCompose(finishedJob -> {
            if (!retryOnReauth || finishedJob.getErrorStatus() != ErrorStatus.REAUTH_REQUIRED) {
                return CompletableFuture.completedFuture(finishedJob);
            }

            SteamLoginServiceResult loginResult = steamLoginService.refreshCachedSession();
            if (loginResult.getResult() != SteamLoginResult.SUCCESS) {
                return CompletableFuture.completedFuture(finishedJob);
            }

            finishedJob.setErrorStatus(null);
            return enqueueJob(finishedJob, false);
        });
    }
}
