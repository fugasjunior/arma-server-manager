package cz.forgottenempire.arma3servergui.workshop;

import cz.forgottenempire.arma3servergui.common.InstallationStatus;
import cz.forgottenempire.arma3servergui.common.PathsFactory;
import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
import cz.forgottenempire.arma3servergui.steamcmd.SteamCmdJob;
import cz.forgottenempire.arma3servergui.steamcmd.SteamCmdService;
import cz.forgottenempire.arma3servergui.util.FileSystemUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class WorkshopInstallerService {

    private final PathsFactory pathsFactory;
    private final WorkshopModsService modsService;
    private final SteamCmdService steamCmdService;

    @Autowired
    public WorkshopInstallerService(
            PathsFactory pathsFactory,
            WorkshopModsService modsService,
            SteamCmdService steamCmdService
    ) {
        this.pathsFactory = pathsFactory;
        this.modsService = modsService;
        this.steamCmdService = steamCmdService;
    }

    public void installOrUpdateMods(Collection<WorkshopMod> mods) {
        mods.forEach(mod -> {
            mod.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);
            mod.setErrorStatus(null);
            modsService.saveMod(mod);
        });

        mods.forEach(mod -> steamCmdService.installOrUpdateWorkshopMod(mod)
                .thenAcceptAsync(steamCmdJob -> handleInstallation(mod, steamCmdJob)));
    }

    public void uninstallMod(WorkshopMod mod) {
        File modDirectory = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType()).toFile();
        try {
            deleteSymlink(mod);
            FileUtils.deleteDirectory(modDirectory);
        } catch (NoSuchFileException ignored) {
        } catch (IOException e) {
            log.error("Could not delete mod (directory {}) due to {}", modDirectory, e.toString());
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
            log.info("Mod {} (id {}) successfully downloaded, now installing",
                    mod.getName(), mod.getId());
            installMod(mod);
        }
        modsService.saveMod(mod);
    }

    private void installMod(WorkshopMod mod) {
        try {
            convertModFilesToLowercase(mod);
            copyBiKeys(mod);
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
        log.info("Converting mod file names to lowercase");
        File modDir = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType()).toFile();
        FileSystemUtils.directoryToLowercase(modDir);
        log.info("Converting file names to lowercase done");
    }

    private void copyBiKeys(WorkshopMod mod) throws IOException {
        String[] extensions = new String[]{"bikey"};
        File modDirectory = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType()).toFile();

        for (Iterator<File> it = FileUtils.iterateFiles(modDirectory, extensions, true); it.hasNext(); ) {
            File key = it.next();
            log.info("Copying BiKey {} to server", key.getName());
            FileUtils.copyFile(key, pathsFactory.getServerKeyPath(key.getName(), mod.getServerType()).toFile());
        }
    }

    private void createSymlink(WorkshopMod mod) throws IOException {
        // create symlink to server directory
        Path linkPath = pathsFactory.getModLinkPath(mod.getNormalizedName(), mod.getServerType());
        Path targetPath = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType());

        log.info("Creating symlink - link {}, target {}", linkPath, targetPath);
        if (!Files.isSymbolicLink(linkPath)) {
            Files.createSymbolicLink(linkPath, targetPath);
        }
    }

    private void updateModInfo(WorkshopMod mod) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        mod.setLastUpdated(formatter.format(new Date()));
        mod.setFileSize(getActualSizeOfMod(mod.getId(), mod.getServerType()));
    }

    private void deleteSymlink(WorkshopMod mod) throws IOException {
        Path linkPath = pathsFactory.getModLinkPath(mod.getNormalizedName(), mod.getServerType());
        log.info("Deleting symlink {}", linkPath);
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
