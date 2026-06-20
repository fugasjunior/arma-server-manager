package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;
import cz.forgottenempire.servermanager.common.exceptions.ServerNotInitializedException;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import org.springframework.http.HttpStatus;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdJob;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdService;
import cz.forgottenempire.servermanager.util.FileSystemUtils;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadata;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
class WorkshopInstallerService {

    private final PathsFactory pathsFactory;
    private final WorkshopModsService modsService;
    private final SteamCmdService steamCmdService;
    private final ServerInstallationService installationService;
    private final ModMetadataService metadataService;

    @Autowired
    public WorkshopInstallerService(
            PathsFactory pathsFactory,
            WorkshopModsService modsService,
            SteamCmdService steamCmdService,
            ServerInstallationService installationService,
            ModMetadataService metadataService) {
        this.pathsFactory = pathsFactory;
        this.modsService = modsService;
        this.steamCmdService = steamCmdService;
        this.installationService = installationService;
        this.metadataService = metadataService;
    }

    public void installOrUpdateMods(Collection<WorkshopMod> mods) {
        installOrUpdateMods(mods, false);
    }

    public void installOrUpdateMods(Collection<WorkshopMod> mods, boolean forceUpdate) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                CompletableFuture.runAsync(() -> resolveAndInstall(mods, forceUpdate));
            }
        });
    }

    public void uninstallMod(WorkshopMod mod) {
        if (mod.getServerType() == null) {
            // mod was never installed (metadata lookup failed before download), nothing to clean up
            log.info("Mod {} ({}) had no server type — skipping filesystem cleanup", mod.getName(), mod.getId());
            return;
        }
        File modDirectory = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType()).toFile();
        try {
            deleteBiKeys(mod);
            deleteSymlink(mod);
            FileUtils.deleteDirectory(modDirectory);
        } catch (NoSuchFileException ignored) {
        } catch (IOException e) {
            log.error("Could not delete mod (directory {})", modDirectory, e);
            throw new RuntimeException(e);
        }
        log.info("Mod {} ({}) successfully deleted", mod.getName(), mod.getId());
    }

    /**
     * Resolves metadata for all mods in a single batched Steam API call, validates each mod,
     * then enqueues the download for valid mods. Runs asynchronously after the transaction commits.
     */
    private void resolveAndInstall(Collection<WorkshopMod> mods, boolean forceUpdate) {
        List<Long> modIds = mods.stream().map(WorkshopMod::getId).toList();
        Map<Long, ModMetadata> metadataMap = metadataService.fetchModMetadata(modIds);

        List<WorkshopMod> validMods = new ArrayList<>();
        for (WorkshopMod mod : mods) {
            ModMetadata metadata = metadataMap.get(mod.getId());
            if (metadata == null) {
                log.error("Mod id {} not found on Steam Workshop — marking as error", mod.getId());
                mod.setInstallationStatus(InstallationStatus.ERROR);
                mod.setErrorStatus(ErrorStatus.NO_MATCH);
                modsService.saveMod(mod);
                continue;
            }
            mod.setName(metadata.name());
            try {
                setModServerType(mod, metadata.consumerAppId());
                validateServerInitialized(mod);
                if (!forceUpdate
                        && mod.getInstallationStatus() == InstallationStatus.FINISHED
                        && verifyModDirectoryExists(mod.getId(), mod.getServerType())) {
                    log.info("Mod '{}' (ID {}) is already downloaded; skipping SteamCMD and refreshing installation",
                            mod.getName(), mod.getId());
                    refreshInstalledMod(mod);
                    continue;
                }
                validMods.add(mod);
            } catch (ModNotConsumedByGameException e) {
                log.error("Mod '{}' (id {}) failed validation: {}", mod.getName(), mod.getId(), e.getMessage());
                mod.setInstallationStatus(InstallationStatus.ERROR);
                mod.setErrorStatus(ErrorStatus.NOT_CONSUMED_BY_GAME);
                modsService.saveMod(mod);
            } catch (ServerNotInitializedException e) {
                log.error("Mod '{}' (id {}) failed validation: {}", mod.getName(), mod.getId(), e.getMessage());
                mod.setInstallationStatus(InstallationStatus.ERROR);
                mod.setErrorStatus(ErrorStatus.SERVER_NOT_INSTALLED);
                modsService.saveMod(mod);
            }
        }

        if (validMods.isEmpty()) {
            return;
        }

        log.info("Metadata resolved for {} mod(s), enqueueing download", validMods.size());
        beforeDownload(validMods);
        var future = steamCmdService.installOrUpdateWorkshopMods(validMods);
        future.thenAcceptAsync(steamCmdJob -> steamCmdJob.getRelatedWorkshopMods().forEach(
                mod -> handleInstallation(mod, steamCmdJob)
        ));
    }

    /**
     * Hook called with valid mods just before the SteamCMD download is enqueued.
     * No-op in production; overridden in E2E test profile.
     */
    protected void beforeDownload(Collection<WorkshopMod> mods) {
        // no-op
    }

    private void setModServerType(WorkshopMod mod, String consumerAppId) {
        if (Constants.GAME_IDS.get(ServerType.ARMA3).toString().equals(consumerAppId)) {
            mod.setServerType(ServerType.ARMA3);
        } else if (Constants.GAME_IDS.get(ServerType.DAYZ).toString().equals(consumerAppId)) {
            mod.setServerType(ServerType.DAYZ);
        } else {
            log.warn("Tried to install mod ID {} which is not consumed by any of the supported servers", mod.getId());
            throw new ModNotConsumedByGameException(
                    "The mod " + mod.getId() + " is not consumed by any supported game");
        }
    }

    private void validateServerInitialized(WorkshopMod mod) {
        if (mod.getServerType() == ServerType.ARMA3 && isServerNotInitialized(ServerType.ARMA3)) {
            throw new ServerNotInitializedException("Mod installation failed: Arma 3 server is not installed");
        }
        if (mod.getServerType() == ServerType.DAYZ && isServerNotInitialized(ServerType.DAYZ)
                && isServerNotInitialized(ServerType.DAYZ_EXP)) {
            throw new ServerNotInitializedException(
                    "Mod installation failed: Neither DayZ nor DayZ Experimental server is installed");
        }
    }

    private boolean isServerNotInitialized(ServerType serverType) {
        return !installationService.isServerInstalled(serverType);
    }

    void handleInstallation(WorkshopMod mod, SteamCmdJob steamCmdJob) {
        boolean downloaded = verifyModDirectoryExists(mod.getId(), mod.getServerType());

        if (downloaded) {
            if (steamCmdJob.getErrorStatus() != null) {
                log.info("Mod '{}' (ID {}) was downloaded before the SteamCMD batch failed; installing it normally",
                        mod.getName(), mod.getId());
            } else {
                log.info("Mod '{}' (ID {}) successfully downloaded, now installing", mod.getName(), mod.getId());
            }
            installMod(mod);
        } else if (steamCmdJob.getErrorStatus() != null) {
            log.error("Download of mod '{}' (id {}) failed, reason: {}",
                    mod.getName(), mod.getId(), steamCmdJob.getErrorStatus());
            mod.setInstallationStatus(InstallationStatus.ERROR);
            mod.setErrorStatus(steamCmdJob.getErrorStatus());
        } else {
            log.error("Could not find downloaded mod directory for mod '{}' (id {}) " +
                    "even though download finished successfully", mod.getName(), mod.getId());
            mod.setInstallationStatus(InstallationStatus.ERROR);
            mod.setErrorStatus(ErrorStatus.GENERIC);
        }

        modsService.saveMod(mod);
    }

    private void installMod(WorkshopMod mod) {
        try {
            convertModFilesToLowercase(mod);
            updateBiKeys(mod);
            createSymlink(mod);
            updateModInfo(mod);
            mod.setInstallationStatus(InstallationStatus.FINISHED);
            log.info("Mod '{}' (ID {}) successfully installed", mod.getName(), mod.getId());
        } catch (Exception e) {
            log.error("Failed to install mod {} (ID {})", mod.getName(), mod.getId(), e);
            mod.setInstallationStatus(InstallationStatus.ERROR);
            mod.setErrorStatus(ErrorStatus.IO);
        }
    }

    void refreshInstalledMod(WorkshopMod mod) {
        installMod(mod);
        modsService.saveMod(mod);
    }

    private void convertModFilesToLowercase(WorkshopMod mod) throws IOException {
        File modDir = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType()).toFile();
        FileSystemUtils.directoryToLowercase(modDir);
    }

    private void updateBiKeys(WorkshopMod mod) throws IOException {
        deleteBiKeys(mod);
        installNewBiKeys(mod);
    }

    private void deleteBiKeys(WorkshopMod mod) {
        mod.getBiKeys().forEach(biKey -> {
            for (ServerType serverType : installationService.getInstalledRelatedServerTypes(mod.getServerType())) {
                File keyFile = pathsFactory.getServerKeyPath(biKey, serverType).toFile();
                FileUtils.deleteQuietly(keyFile);
            }
        });
    }

    private void installNewBiKeys(WorkshopMod mod) throws IOException {
        String[] extensions = new String[]{"bikey"};
        File modDirectory = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType()).toFile();

        for (Iterator<File> it = FileUtils.iterateFiles(modDirectory, extensions, true); it.hasNext(); ) {
            File key = it.next();
            mod.addBiKey(key.getName());
            for (ServerType serverType : installationService.getInstalledRelatedServerTypes(mod.getServerType())) {
                log.debug("Copying BiKey {} to server {}", key.getName(), serverType);
                FileUtils.copyFile(key, pathsFactory.getServerKeyPath(key.getName(), serverType).toFile());
            }
        }
    }

    private void createSymlink(WorkshopMod mod) throws IOException {
        Path targetPath = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType());

        for (ServerType serverType : installationService.getInstalledRelatedServerTypes(mod.getServerType())) {
            Path linkPath = pathsFactory.getModLinkPath(mod.getNormalizedName(), serverType);
            if (Files.isSymbolicLink(linkPath)) {
                Path existingTarget = Files.readSymbolicLink(linkPath);
                if (!existingTarget.isAbsolute()) {
                    existingTarget = linkPath.getParent().resolve(existingTarget);
                }
                if (existingTarget.startsWith(pathsFactory.getLocalModsBasePath(serverType))) {
                    throw new CustomUserErrorException(
                            "Cannot install workshop mod '" + mod.getName() + "': a local mod symlink already exists at " +
                            linkPath + ". Delete the conflicting local mod first.",
                            HttpStatus.CONFLICT);
                }
                // existing workshop symlink — already linked, skip
            } else {
                log.debug("Creating symlink - link {}, target {}", linkPath, targetPath);
                Files.createSymbolicLink(linkPath, targetPath);
            }
        }
    }

    private void updateModInfo(WorkshopMod mod) {
        mod.setLastUpdated(LocalDateTime.now());
        mod.setFileSize(getActualSizeOfMod(mod.getId(), mod.getServerType()));
    }

    private void deleteSymlink(WorkshopMod mod) throws IOException {
        Path linkPath = pathsFactory.getModLinkPath(mod.getNormalizedName(), mod.getServerType());
        log.debug("Deleting symlink {}", linkPath);
        if (Files.isSymbolicLink(linkPath)) {
            Files.delete(linkPath);
        }
    }

    private boolean verifyModDirectoryExists(Long modId, ServerType type) {
        return pathsFactory.getModInstallationPath(modId, type)
                .toFile()
                .isDirectory();
    }

    // as data about mod size from workshop API are not reliable, find the size of disk instead
    private Long getActualSizeOfMod(Long modId, ServerType type) {
        return FileUtils.sizeOfDirectory(
                pathsFactory.getModInstallationPath(modId, type).toFile()
        );
    }
}
