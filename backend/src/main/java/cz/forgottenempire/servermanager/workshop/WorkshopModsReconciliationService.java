package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadata;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
class WorkshopModsReconciliationService {

    private final WorkshopModsService modsService;
    private final WorkshopInstallerService installerService;
    private final ModMetadataService metadataService;
    private final PathsFactory pathsFactory;
    private final SteamCmdItemInfoRepository itemInfoRepository;

    WorkshopModsReconciliationService(
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

    @Scheduled(fixedDelay = 15 * 60 * 1000L)
    public void reconcileScheduled() {
        reconcile(modsService.getAllMods());
    }

    void reconcile(Collection<WorkshopMod> mods) {
        List<WorkshopMod> modsNeedingMetadata = mods.stream()
                .filter(this::needsMetadata)
                .toList();
        Map<Long, ModMetadata> metadataById = modsNeedingMetadata.isEmpty()
                ? Map.of()
                : metadataService.fetchModMetadata(
                        modsNeedingMetadata.stream().map(WorkshopMod::getId).toList());

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

        Path modPath = pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType());
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
}
