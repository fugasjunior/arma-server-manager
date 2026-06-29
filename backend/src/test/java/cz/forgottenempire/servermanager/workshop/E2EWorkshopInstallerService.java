package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdService;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * E2E override that pre-creates the expected SteamCMD download directory for each mod before
 * delegating to the real installer. The fake SteamCMD process exits 0 but writes no files, so
 * verifyModDirectoryExists() would otherwise fail despite a "successful" download.
 */
@Service
@Primary
@Profile("e2e")
class E2EWorkshopInstallerService extends WorkshopInstallerService {

    private final PathsFactory pathsFactory;

    E2EWorkshopInstallerService(PathsFactory pathsFactory, WorkshopModsService modsService,
                                 SteamCmdService steamCmdService, ServerInstallationService installationService,
                                 ModMetadataService metadataService, SteamCmdItemInfoRepository itemInfoRepository) {
        super(pathsFactory, modsService, steamCmdService, installationService, metadataService, itemInfoRepository);
        this.pathsFactory = pathsFactory;
    }

    @Override
    protected void beforeDownload(Collection<WorkshopMod> mods) {
        mods.forEach(mod ->
                pathsFactory.getModInstallationPath(mod.getId(), mod.getServerType()).toFile().mkdirs());
    }
}
