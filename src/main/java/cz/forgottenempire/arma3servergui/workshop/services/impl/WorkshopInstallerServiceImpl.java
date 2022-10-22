package cz.forgottenempire.arma3servergui.workshop.services.impl;

import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.common.util.FileSystemUtils;
import cz.forgottenempire.arma3servergui.common.util.SteamCmdWrapper;
import cz.forgottenempire.arma3servergui.workshop.entities.DownloadStatus;
import cz.forgottenempire.arma3servergui.workshop.entities.ErrorStatus;
import cz.forgottenempire.arma3servergui.system.entities.SteamAuth;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod.InstallationStatus;
import cz.forgottenempire.arma3servergui.workshop.repositories.WorkshopModRepository;
import cz.forgottenempire.arma3servergui.system.services.SteamAuthService;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopFileDetailsService;
import cz.forgottenempire.arma3servergui.workshop.services.WorkshopInstallerService;
import cz.forgottenempire.steamcmd.SteamCmdParameterBuilder;
import cz.forgottenempire.steamcmd.SteamCmdParameters;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
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
        mod.setInstallationStatus(InstallationStatus.INSTALLATION_QUEUED);
        modRepository.save(mod);

        executor.submit(() -> {
            log.info("Starting download of mod {} (id {})", mod.getName(), mod.getId());
            initializeModDownloadStatus(mod);
            if (!downloadMod(mod) && !installMod(mod)) {
                return;
            }
            updateModInfo(mod);
            log.info("Mod {} (id {}) successfully installed ({} left in queue)", mod.getName(), mod.getId(),
                    executor.getQueue().size());
        });
    }

    private boolean downloadMod(WorkshopMod mod) {
        DownloadStatus downloadStatus = executeWorkshopModDownload(mod.getId());
        if (!downloadStatus.isSuccess() || !verifyModDirectoryExists(mod.getId())) {
            log.error("Failed to download mod {} ({}) ", mod.getName(), mod.getId());
            mod.setInstallationStatus(InstallationStatus.ERROR);
            mod.setErrorStatus(downloadStatus.getErrorStatus());
            modRepository.save(mod);
            return false;
        }
        return true;
    }

    private boolean installMod(WorkshopMod mod) {
        try {
            convertModFilesToLowercase(mod);
            copyBiKeys(mod.getId());
            createSymlink(mod);
        } catch (Exception e) {
            log.error("Failed to install mod {} ({}) due to: {}", mod.getName(), mod.getId(), e.getMessage());
            mod.setInstallationStatus(InstallationStatus.ERROR);
            mod.setErrorStatus(ErrorStatus.IO);
            modRepository.save(mod);
            return false;
        }
        return true;
    }

    private void convertModFilesToLowercase(WorkshopMod mod) throws IOException {
        log.info("Converting mod file names to lowercase");
        File modDir = new File(getModDirectoryPath(mod.getId()));
        FileSystemUtils.directoryToLowercase(modDir);
        log.info("Converting file names to lowercase done");
    }

    private void copyBiKeys(Long modId) throws IOException {
        String[] extensions = new String[]{"bikey"};
        File modDirectory = new File(getModDirectoryPath(modId));

        if (!verifyModDirectoryExists(modId)) {
            log.error("Can not access mod directory {}", modId);
            return;
        }

        for (Iterator<File> it = FileUtils.iterateFiles(modDirectory, extensions, true); it.hasNext(); ) {
            File key = it.next();
            log.info("Copying BiKey {} to server", key.getName());
            FileUtils.copyFile(key,
                    new File(serverPath + File.separatorChar + "keys" + File.separatorChar + key.getName()));
        }
    }

    private void createSymlink(WorkshopMod mod) throws IOException {
        // create symlink to server directory
        Path linkPath = Path.of(getSymlinkTargetPath(mod.getNormalizedName()));
        Path targetPath = Path.of(getModDirectoryPath(mod.getId()));

        log.info("Creating symlink - link {}, target {}", linkPath, targetPath);
        if (!Files.isSymbolicLink(linkPath)) {
            Files.createSymbolicLink(linkPath, targetPath);
        }
    }

    private void updateModInfo(WorkshopMod mod) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        mod.setLastUpdated(formatter.format(new Date()));
        mod.setFileSize(getActualSizeOfMod(mod.getId()));
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        modRepository.save(mod);
    }

    private void initializeModDownloadStatus(WorkshopMod mod) {
        mod.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);
        mod.setErrorStatus(null);
        modRepository.save(mod);
    }

    @Override
    public void deleteMod(WorkshopMod mod) {
        File modDirectory = new File(getDownloadPath() + File.separatorChar + mod.getId());
        try {
            deleteSymlink(mod);
            FileUtils.deleteDirectory(modDirectory);
            modRepository.delete(mod);
        } catch (IOException e) {
            log.error("Could not delete directory {} due to {}", modDirectory, e.toString());
            throw new RuntimeException(e);
        }
        log.info("Mod {} ({}) successfully deleted", mod.getName(), mod.getId());
    }

    @Override
    public void updateAllMods(SteamAuth auth) {
        Set<Long> modIds = getModIds();

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
    }

    private Set<Long> getModIds() {
        File modDirectory = new File(getDownloadPath());
        // find all installed mods in installation folder
        Set<Long> modIds = new HashSet<>();
        try (Stream<Path> files = Files.list(modDirectory.toPath())) {
            files.forEach(p -> {
                        try {
                            long id = Long.parseLong(p.getFileName().toString());
                            modIds.add(id);
                        } catch (NumberFormatException ignored) {
                        }
                    }
            );
        } catch (IOException e) {
            log.error("Failed to collect existing mod directory IDs due to {}", e.toString());
            throw new RuntimeException(e);
        }

        // add all missing mods from db
        modRepository.findAll().stream()
                .map(WorkshopMod::getId)
                .forEach(modIds::add);
        return modIds;
    }

    private DownloadStatus executeWorkshopModDownload(Long modId) {
        SteamAuth auth = steamAuthService.getAuthAccount();
        SteamCmdParameters parameters = new SteamCmdParameterBuilder()
                .withLogin(auth.getUsername(), auth.getPassword(), auth.getSteamGuardToken())
                .withInstallDir(downloadPath)
                .withWorkshopItemInstall(Constants.STEAM_ARMA3_ID, modId, true)
                .build();

        return steamCmd.execute(parameters);
    }

    private void deleteSymlink(WorkshopMod mod) throws IOException {
        Path linkPath = Path.of(getSymlinkTargetPath(mod.getNormalizedName()));
        log.info("Deleting symlink {}", linkPath);
        Files.delete(linkPath);
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
