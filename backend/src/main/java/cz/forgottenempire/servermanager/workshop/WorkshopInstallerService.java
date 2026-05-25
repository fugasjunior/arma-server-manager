package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdJob;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdService;
import cz.forgottenempire.servermanager.util.FileSystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import cz.forgottenempire.servermanager.common.exceptions.CustomUserErrorException;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;

@Service
@Slf4j
class WorkshopInstallerService {

    private final PathsFactory pathsFactory;
    private final WorkshopModsService modsService;
    private final SteamCmdService steamCmdService;
    private final ServerInstallationService installationService;

    @Autowired
    public WorkshopInstallerService(
            PathsFactory pathsFactory,
            WorkshopModsService modsService,
            SteamCmdService steamCmdService,
            ServerInstallationService installationService) {
        this.pathsFactory = pathsFactory;
        this.modsService = modsService;
        this.steamCmdService = steamCmdService;
        this.installationService = installationService;
    }

    public void installOrUpdateMods(Collection<WorkshopMod> mods) {
        var future = steamCmdService.installOrUpdateWorkshopMods(mods);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                future.thenAcceptAsync(steamCmdJob -> steamCmdJob.getRelatedWorkshopMods().forEach(
                        mod -> handleInstallation(mod, steamCmdJob)
                ));
            }
        });
    }

    public void uninstallMod(WorkshopMod mod) {
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

    private void handleInstallation(WorkshopMod mod, SteamCmdJob steamCmdJob) {
        if (steamCmdJob.getErrorStatus() != null) {
            log.error("Download of mod '{}' (id {}) failed, reason: {}",
                    mod.getName(), mod.getId(), steamCmdJob.getErrorStatus());
            mod.setInstallationStatus(InstallationStatus.ERROR);
            mod.setErrorStatus(steamCmdJob.getErrorStatus());
        } else if (!verifyModDirectoryExists(mod.getId(), mod.getServerType())) {
            log.error("Could not find downloaded mod directory for mod '{}' (id {}) " +
                    "even though download finished successfully", mod.getName(), mod.getId());
            mod.setInstallationStatus(InstallationStatus.ERROR);
            mod.setErrorStatus(ErrorStatus.GENERIC);
        } else {
            log.info("Mod '{}' (ID {}) successfully downloaded, now installing", mod.getName(), mod.getId());
            installMod(mod);
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
