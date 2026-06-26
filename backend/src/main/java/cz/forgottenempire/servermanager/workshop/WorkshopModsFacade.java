package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadata;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.annotation.Nullable;
import org.apache.commons.io.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class WorkshopModsFacade {

    private final WorkshopModsService modsService;
    private final WorkshopInstallerService installerService;
    private final ModMetadataService metadataService;
    private final PathsFactory pathsFactory;
    private final SteamCmdItemInfoRepository itemInfoRepository;

    @Autowired
    public WorkshopModsFacade(
            WorkshopModsService modsService,
            WorkshopInstallerService installerService,
            ModMetadataService metadataService,
            PathsFactory pathsFactory,
            SteamCmdItemInfoRepository itemInfoRepository) {
        this.modsService = modsService;
        this.installerService = installerService;
        this.metadataService = metadataService;
        this.pathsFactory = pathsFactory;
        this.itemInfoRepository = itemInfoRepository;
    }

    public Optional<WorkshopMod> getMod(long id) {
        return modsService.getMod(id).map(this::refreshIncompleteDetails);
    }

    public Collection<WorkshopMod> getAllMods() {
        return refreshIncompleteDetails(modsService.getAllMods());
    }

    public Collection<WorkshopMod> getAllMods(@Nullable ServerType filter) {
        if (filter == null) {
            return getAllMods();
        }
        ServerType normalizedFilter = filter == ServerType.DAYZ_EXP ? ServerType.DAYZ : filter;
        return refreshIncompleteDetails(modsService.getAllMods()).stream()
                .filter(mod -> normalizedFilter == mod.getServerType())
                .toList();
    }

    private WorkshopMod refreshIncompleteDetails(WorkshopMod mod) {
        refreshIncompleteDetails(List.of(mod));
        return mod;
    }

    private Collection<WorkshopMod> refreshIncompleteDetails(Collection<WorkshopMod> mods) {
        List<WorkshopMod> modsNeedingMetadata = mods.stream()
                .filter(this::needsMetadata)
                .toList();
        Map<Long, ModMetadata> metadataById = modsNeedingMetadata.isEmpty()
                ? Map.of()
                : metadataService.fetchModMetadata(modsNeedingMetadata.stream().map(WorkshopMod::getId).toList());

        mods.forEach(mod -> {
            boolean changed = applyMetadataIfAvailable(mod, metadataById.get(mod.getId()));
            changed |= refreshFileSizeIfAvailable(mod);
            if (refreshStaleInstallationIfComplete(mod)) {
                return;
            }
            if (changed) {
                modsService.saveMod(mod);
            }
        });

        return mods;
    }

    private boolean needsMetadata(WorkshopMod mod) {
        return mod.getName() == null || mod.getName().isBlank() || mod.getServerType() == null;
    }

    private boolean applyMetadataIfAvailable(WorkshopMod mod, ModMetadata metadata) {
        if (metadata == null) {
            return false;
        }

        boolean changed = false;
        if (mod.getName() == null || mod.getName().isBlank()) {
            mod.setName(metadata.name());
            changed = true;
        }
        if (mod.getServerType() == null) {
            ServerType serverType = serverTypeFromConsumerAppId(metadata.consumerAppId());
            if (serverType != null) {
                mod.setServerType(serverType);
                changed = true;
            }
        }
        return changed;
    }

    private ServerType serverTypeFromConsumerAppId(String consumerAppId) {
        if (Constants.GAME_IDS.get(ServerType.ARMA3).toString().equals(consumerAppId)) {
            return ServerType.ARMA3;
        }
        if (Constants.GAME_IDS.get(ServerType.DAYZ).toString().equals(consumerAppId)) {
            return ServerType.DAYZ;
        }
        return null;
    }

    private boolean refreshFileSizeIfAvailable(WorkshopMod mod) {
        if (mod.getServerType() == null || mod.getFileSize() != null && mod.getFileSize() > 0) {
            return false;
        }

        var modPath = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType());
        if (!modPath.toFile().isDirectory()) {
            return false;
        }

        long actualSize = FileUtils.sizeOfDirectory(modPath.toFile());
        if (mod.getFileSize() != null && mod.getFileSize() == actualSize) {
            return false;
        }

        mod.setFileSize(actualSize);
        return true;
    }

    private boolean refreshStaleInstallationIfComplete(WorkshopMod mod) {
        if (mod.getInstallationStatus() != InstallationStatus.INSTALLATION_IN_PROGRESS
                || mod.getServerType() == null
                || itemInfoRepository.get(mod.getId()).isPresent()
                || !isWorkshopItemInstalled(mod)) {
            return false;
        }

        log.info("Repairing stale installation status for workshop mod {} ({}) from SteamCMD manifest",
                mod.getName(), mod.getId());
        installerService.refreshInstalledMod(mod);
        return true;
    }

    private boolean isWorkshopItemInstalled(WorkshopMod mod) {
        Long appId = Constants.GAME_IDS.get(mod.getServerType());
        if (appId == null) {
            return false;
        }

        Path manifestPath = pathsFactory.getModsBasePath()
                .resolve("steamapps")
                .resolve("workshop")
                .resolve("appworkshop_" + appId + ".acf");
        if (!Files.isRegularFile(manifestPath)) {
            return false;
        }

        String expectedLine = "\"" + mod.getId() + "\"";
        try (var lines = Files.lines(manifestPath)) {
            return lines.map(String::trim).anyMatch(expectedLine::equals);
        } catch (IOException e) {
            log.warn("Could not read Steam Workshop manifest {}", manifestPath, e);
            return false;
        }
    }

    @Transactional
    public List<WorkshopMod> saveAndInstallMods(List<Long> ids) {
        return persistAndInstallMods(ids, false);
    }

    @Transactional
    public List<WorkshopMod> updateMods(List<Long> ids) {
        return persistAndInstallMods(ids, true);
    }

    private List<WorkshopMod> persistAndInstallMods(List<Long> ids, boolean forceUpdate) {
        List<WorkshopMod> workshopMods = ids.stream()
                .map(id -> getMod(id).orElse(new WorkshopMod(id)))
                .toList();

        workshopMods.forEach(mod -> {
            if (forceUpdate || mod.getInstallationStatus() != InstallationStatus.FINISHED) {
                mod.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);
            }
            mod.setErrorStatus(null);
        });
        modsService.saveAllModsForInstallation(workshopMods);

        if (forceUpdate) {
            installerService.updateMods(workshopMods);
        } else {
            installerService.installMods(workshopMods);
        }
        return workshopMods;
    }

    @Transactional
    public void updateAllMods() {
        List<Long> allModIds = modsService.getAllMods().stream()
                .map(WorkshopMod::getId)
                .toList();
        updateMods(allModIds);
    }

    public void uninstallMod(long id) {
        WorkshopMod workshopMod = getMod(id)
                .orElseThrow(() -> new NotFoundException("Mod ID " + id + " not found."));
        installerService.uninstallMod(workshopMod);
        modsService.deleteMod(workshopMod);
    }

    public void setModServerOnly(WorkshopMod mod, boolean serverOnly) {
        mod.setServerOnly(serverOnly);
        modsService.saveMod(mod);
    }

    public void setModLoadOnHeadlessClient(WorkshopMod mod, boolean loadOnHeadlessClient) {
        mod.setLoadOnHeadlessClient(loadOnHeadlessClient);
        modsService.saveMod(mod);
    }
}
