package cz.forgottenempire.arma3servergui.services.impl;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.DownloadStatus;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.repositories.WorkshopModRepository;
import cz.forgottenempire.arma3servergui.services.SteamAuthService;
import cz.forgottenempire.arma3servergui.services.WorkshopFileDetailsService;
import cz.forgottenempire.arma3servergui.services.WorkshopInstallerService;
import cz.forgottenempire.arma3servergui.util.FileSystemUtils;
import cz.forgottenempire.arma3servergui.util.SteamCmdWrapper;
import cz.forgottenempire.steamcmd.SteamCmdParameterBuilder;
import cz.forgottenempire.steamcmd.SteamCmdParameters;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WorkshopInstallerServiceImpl implements WorkshopInstallerService {

    @Value("${installDir}")
    private String downloadPath;

    @Value("${serverDir}")
    private String serverPath;

    private WorkshopModRepository modRepository;
    private SteamCmdWrapper steamCmd;
    private WorkshopFileDetailsService workshopFileDetailsService;

    private SteamAuthService steamAuthService;

    private final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    @Override
    public void installOrUpdateMod(SteamAuth auth, final WorkshopMod mod) {
        executor.submit(() -> {
            mod.setInstalled(false);
            mod.setFailed(false);
            modRepository.save(mod);
            log.info("Starting download of mod {} (id {})", mod.getName(), mod.getId());

            DownloadStatus downloadStatus = downloadMod(mod.getId());
            mod.setDownloadStatus(downloadStatus);
            if (!downloadStatus.isSuccess() || !verifyModDirectoryExists(mod.getId())) {
                log.error("Failed to download mod {} ({}) ", mod.getName(), mod.getId());
                downloadStatus.setSuccess(false);
                modRepository.save(mod);
                return;
            }

            convertModFilesToLowercase(mod);
            copyBiKeys(mod.getId());
            createSymlink(mod);

            mod.setInstalled(true);
            mod.setFailed(false);
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            mod.setLastUpdated(formatter.format(new Date()));
            mod.setFileSize(getActualSizeOfMod(mod.getId()));
            modRepository.save(mod);

            log.info("Mod {} (id {}) successfully installed ({} left in queue)", mod.getName(), mod.getId(),
                    executor.getQueue().size());
        });
    }

    @Override
    public boolean deleteMod(WorkshopMod mod) {
        File modDirectory = new File(getDownloadPath() + File.separatorChar + mod.getId());
        try {
            FileUtils.deleteDirectory(modDirectory);
            deleteSymlink(mod);
            modRepository.delete(mod);
        } catch (IOException e) {
            log.error("Could not delete directory {} due to {}", modDirectory, e.toString());
            return false;
        }
        log.info("Mod {} ({}) successfully deleted", mod.getName(), mod.getId());
        return true;
    }

    @Override
    public boolean updateAllMods(SteamAuth auth) {
        File modDirectory = new File(getDownloadPath());

        try {
            // find all installed mods in installation folder
            Set<Long> modIds = Files.list(modDirectory.toPath())
                    .map(p -> {
                        try {
                            return Long.parseLong(p.getFileName().toString());
                        } catch (NumberFormatException e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toSet());

            // add all missing mods from db
            StreamSupport.stream(modRepository.findAll().spliterator(), false)
                    .peek(mod -> {
                        mod.setInstalled(false);
                        mod.setFailed(false);
                        modRepository.save(mod);
                    })
                    .map(WorkshopMod::getId)
                    .forEach(modIds::add);

            // refresh/update all found mods
            for (Long modId : modIds) {
                WorkshopMod mod = modRepository.findById(modId)
                        .orElseGet(() -> {
                            // create mods which were not persisted in db
                            WorkshopMod newMod = new WorkshopMod(modId);
                            newMod.setName(workshopFileDetailsService.getModName(modId));
                            return modRepository.save(newMod);
                        });
                installOrUpdateMod(auth, mod);
            }
        } catch (IOException e) {
            log.error("Failed to refresh mods due to {}", e.toString());
            return false;
        }

        return true;
    }

    private DownloadStatus downloadMod(Long modId) {
        SteamAuth auth = steamAuthService.getAuthAccount();
        SteamCmdParameters parameters = new SteamCmdParameterBuilder()
                .withLogin(auth.getUsername(), auth.getPassword(), auth.getSteamGuardToken())
                .withInstallDir(downloadPath)
                .withWorkshopItemInstall(Constants.STEAM_ARMA3_ID, modId, true)
                .build();

        return steamCmd.execute(parameters);
    }

    private void convertModFilesToLowercase(WorkshopMod mod) {
        log.info("Converting mod file names to lowercase");
        File modDir = new File(getModDirectoryPath(mod.getId()));
        try {
            FileSystemUtils.directoryToLowercase(modDir);
            log.info("Converting file names to lowercase done");
        } catch (Exception e) {
            log.error("Failure when converting to lowercase", e);
        }
    }

    private void createSymlink(WorkshopMod mod) {
        // create symlink to server directory
        Path linkPath = Path.of(getSymlinkTargetPath(mod.getNormalizedName()));
        Path targetPath = Path.of(getModDirectoryPath(mod.getId()));

        log.info("Creating symlink - link {}, target {}", linkPath, targetPath);

        try {
            if (!Files.isSymbolicLink(linkPath)) {
                Files.createSymbolicLink(linkPath, targetPath);
            }
        } catch (IOException e) {
            log.error("Failed to create symlink for mod {} ({}) due to {} ",
                    mod.getName(), mod.getId(), e.toString());
        }
    }

    private void deleteSymlink(WorkshopMod mod) {
        Path linkPath = Path.of(getSymlinkTargetPath(mod.getNormalizedName()));
        log.info("Deleting symlink {}", linkPath);
        try {
            Files.delete(linkPath);
        } catch (IOException e) {
            log.warn("Could not delete symlink {} due to {}", linkPath, e.toString());
        }
    }

    // copies all .bikey files to the server keys folder
    private void copyBiKeys(Long modId) {
        String[] extensions = new String[]{"bikey"};
        File modDirectory = new File(getModDirectoryPath(modId));

        if (!verifyModDirectoryExists(modId)) {
            log.error("Can not access mod directory {}", modId);
            return;
        }

        for (Iterator<File> it = FileUtils.iterateFiles(modDirectory, extensions, true); it.hasNext(); ) {
            File key = it.next();
            try {
                log.info("Copying bikey {} to server", key.getName());
                FileUtils.copyFile(key,
                        new File(serverPath + File.separatorChar + "keys" + File.separatorChar + key.getName()));
            } catch (IOException e) {
                log.error("Could not copy bikeys due to {}", e.toString());
            }
        }
    }

    private boolean verifyModDirectoryExists(Long modId) {
        return new File(getModDirectoryPath(modId)).isDirectory();
    }

    // as data about mod size from workshop API are not reliable, find the size of disk instead
    private Long getActualSizeOfMod(Long modId) {
        return FileUtils.sizeOfDirectory(new File(getModDirectoryPath(modId)));
    }

    private String getModDirectoryPath(Long modId) {
        return getDownloadPath() + File.separatorChar + modId;
    }

    private String getSymlinkTargetPath(String name) {
        return serverPath + File.separatorChar + name;
    }

    private String getDownloadPath() {
        return downloadPath
                + File.separatorChar + "steamapps"
                + File.separatorChar + "workshop"
                + File.separatorChar + "content"
                + File.separatorChar + Constants.STEAM_ARMA3_ID;
    }

    @Autowired
    public void setSteamCmd(SteamCmdWrapper steamCmd) {
        this.steamCmd = steamCmd;
    }

    @Autowired
    public void setModRepository(WorkshopModRepository modRepository) {
        this.modRepository = modRepository;
    }

    @Autowired
    public void setWorkshopFileDetailsService(WorkshopFileDetailsService workshopFileDetailsService) {
        this.workshopFileDetailsService = workshopFileDetailsService;
    }

    @Autowired
    public void setSteamAuthService(SteamAuthService steamAuthService) {
        this.steamAuthService = steamAuthService;
    }
}
