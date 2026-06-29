package cz.forgottenempire.servermanager.localmod;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import cz.forgottenempire.servermanager.util.FileSystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
class LocalModInstallerService {

    private static final List<ServerType> SUPPORTED_TYPES = List.of(ServerType.ARMA3, ServerType.DAYZ);

    private final PathsFactory pathsFactory;
    private final LocalModService modService;
    private final ServerInstallationService installationService;

    @Autowired
    public LocalModInstallerService(
            PathsFactory pathsFactory,
            LocalModService modService,
            ServerInstallationService installationService) {
        this.pathsFactory = pathsFactory;
        this.modService = modService;
        this.installationService = installationService;
    }

    public void syncAllMods() {
        SUPPORTED_TYPES.forEach(this::syncMods);
    }

    public void syncMods(ServerType serverType) {
        try {
            Path baseModsPath = pathsFactory.getLocalModsBasePath(serverType);
            if (!Files.exists(baseModsPath)) {
                log.info("Local mods base path does not exist for {}: {}", serverType, baseModsPath);
                return;
            }

            Set<String> presentDirs = getPresentDirectoryNames(baseModsPath);
            List<String> dbModNames = modService.getNamesForServerType(serverType);

            installNewMods(presentDirs, dbModNames, serverType);
            refreshExistingMods(presentDirs, serverType);
            removeDeletedMods(presentDirs, serverType);

            log.info("Local mods sync completed for {}", serverType);
        } catch (Exception e) {
            log.error("Error syncing local mods for {}", serverType, e);
            throw new RuntimeException(e);
        }
    }

    private void installNewMods(Set<String> presentDirs, List<String> dbModNames, ServerType serverType) {
        for (String dirName : presentDirs) {
            if (!dbModNames.contains(dirName)) {
                log.debug("Found new local mod directory: {}", dirName);
                installMod(dirName, serverType);
            }
        }
    }

    private void refreshExistingMods(Set<String> presentDirs, ServerType serverType) {
        for (LocalMod mod : modService.getAllMods(serverType)) {
            if (presentDirs.contains(mod.getName())) {
                log.debug("Updating existing local mod: {}", mod.getName());
                updateBiKeys(mod);
                createSymlink(mod);
                mod.setFileSize(getActualSizeOfMod(mod.getName(), serverType));
                modService.saveMod(mod);
            }
        }
    }

    private void removeDeletedMods(Set<String> presentDirs, ServerType serverType) {
        for (LocalMod mod : modService.getAllMods(serverType)) {
            if (!presentDirs.contains(mod.getName())) {
                log.debug("Local mod directory deleted, removing from DB: {}", mod.getName());
                uninstallMod(mod);
            }
        }
    }

    private void installMod(String dirName, ServerType serverType) {
        try {
            LocalMod mod = new LocalMod();
            mod.setName(dirName);
            mod.setServerType(serverType);
            mod.setUploadedAt(LocalDateTime.now());

            directoryToLowercase(dirName, serverType);
            installNewBiKeys(mod);
            createSymlink(mod);
            mod.setFileSize(getActualSizeOfMod(dirName, serverType));

            modService.saveMod(mod);
            log.info("Local mod '{}' successfully installed for {}", dirName, serverType);
        } catch (Exception e) {
            log.error("Failed to install local mod {} for {}", dirName, serverType, e);
            throw new RuntimeException(e);
        }
    }

    private void uninstallMod(LocalMod mod) {
        try {
            deleteBiKeys(mod);
            deleteSymlink(mod);
            modService.deleteMod(mod.getId());
            log.info("Local mod '{}' successfully uninstalled", mod.getName());
        } catch (Exception e) {
            log.error("Failed to uninstall local mod {}", mod.getName(), e);
            throw new RuntimeException(e);
        }
    }

    private void directoryToLowercase(String dirName, ServerType serverType) throws IOException {
        File modDir = pathsFactory.getLocalModPath(dirName, serverType).toFile();
        FileSystemUtils.directoryToLowercase(modDir);
    }

    private void updateBiKeys(LocalMod mod) {
        deleteBiKeys(mod);
        mod.getBiKeys().clear();
        installNewBiKeys(mod);
    }

    private void installNewBiKeys(LocalMod mod) {
        try {
            String[] extensions = new String[]{"bikey"};
            File modDirectory = pathsFactory.getLocalModPath(mod.getName(), mod.getServerType()).toFile();

            if (!modDirectory.isDirectory()) {
                log.warn("Local mod directory does not exist: {}", modDirectory);
                return;
            }

            for (Iterator<File> it = FileUtils.iterateFiles(modDirectory, extensions, true); it.hasNext(); ) {
                File key = it.next();
                mod.addBiKey(key.getName());
                if (mod.getServerType() == ServerType.ARMA3) {
                    // Arma 3 keys are derived per-instance at start; not copied to a shared dir
                    continue;
                }
                for (ServerType serverType : installationService.getInstalledRelatedServerTypes(mod.getServerType())) {
                    log.debug("Copying BiKey {} to server {}", key.getName(), serverType);
                    FileUtils.copyFile(key, pathsFactory.getServerKeyPath(key.getName(), serverType).toFile());
                }
            }
        } catch (IOException e) {
            log.error("Failed to install bikeys for local mod {}", mod.getName(), e);
            throw new RuntimeException(e);
        }
    }

    private void deleteBiKeys(LocalMod mod) {
        if (mod.getServerType() == ServerType.ARMA3) {
            // Arma 3 keys are derived per-instance at start; no shared dir to delete from
            return;
        }
        mod.getBiKeys().forEach(biKey -> {
            for (ServerType serverType : installationService.getInstalledRelatedServerTypes(mod.getServerType())) {
                File keyFile = pathsFactory.getServerKeyPath(biKey, serverType).toFile();
                FileUtils.deleteQuietly(keyFile);
            }
        });
    }

    private void createSymlink(LocalMod mod) {
        try {
            Path targetPath = pathsFactory.getLocalModPath(mod.getName(), mod.getServerType());

            for (ServerType serverType : installationService.getInstalledRelatedServerTypes(mod.getServerType())) {
                Path linkPath = pathsFactory.getLocalModLinkPath(mod.getName(), serverType);
                if (Files.isSymbolicLink(linkPath)) {
                    Path existingTarget = Files.readSymbolicLink(linkPath);
                    if (!existingTarget.isAbsolute()) {
                        existingTarget = linkPath.getParent().resolve(existingTarget);
                    }
                    if (existingTarget.equals(targetPath)) {
                        log.debug("Symlink already exists and points to correct target: {}", linkPath);
                        continue;
                    } else if (!existingTarget.startsWith(pathsFactory.getLocalModsBasePath(serverType))) {
                        throw new RuntimeException(
                                "Cannot install local mod '" + mod.getName() + "': a workshop mod symlink already exists at " +
                                linkPath + ". Delete the conflicting workshop mod first.");
                    }
                }

                if (!Files.isSymbolicLink(linkPath) && Files.exists(linkPath)) {
                    throw new RuntimeException(
                            "Cannot install local mod '" + mod.getName() + "': a non-symlink file already exists at " +
                            linkPath);
                }

                if (!Files.isSymbolicLink(linkPath)) {
                    log.debug("Creating symlink - link {}, target {}", linkPath, targetPath);
                    Files.createSymbolicLink(linkPath, targetPath);
                }
            }
        } catch (IOException e) {
            log.error("Failed to create symlink for local mod {}", mod.getName(), e);
            throw new RuntimeException(e);
        }
    }

    private void deleteSymlink(LocalMod mod) {
        try {
            Path linkPath = pathsFactory.getLocalModLinkPath(mod.getName(), mod.getServerType());
            log.debug("Deleting symlink {}", linkPath);
            if (Files.isSymbolicLink(linkPath)) {
                Files.delete(linkPath);
            }
        } catch (IOException e) {
            log.error("Failed to delete symlink for local mod {}", mod.getName(), e);
            throw new RuntimeException(e);
        }
    }

    private Long getActualSizeOfMod(String dirName, ServerType serverType) {
        return FileUtils.sizeOfDirectory(
                pathsFactory.getLocalModPath(dirName, serverType).toFile()
        );
    }

    private Set<String> getPresentDirectoryNames(Path baseModsPath) throws IOException {
        Set<String> dirNames = new HashSet<>();
        try (var stream = Files.list(baseModsPath)) {
            stream.filter(Files::isDirectory)
                    .forEach(path -> dirNames.add(path.getFileName().toString()));
        }
        return dirNames;
    }
}
