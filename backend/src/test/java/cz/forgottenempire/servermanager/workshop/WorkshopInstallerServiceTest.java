package cz.forgottenempire.servermanager.workshop;

import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdJob;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdService;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfo;
import cz.forgottenempire.servermanager.steamcmd.outputprocessor.SteamCmdItemInfoRepository;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadata;
import cz.forgottenempire.servermanager.workshop.metadata.ModMetadataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkshopInstallerServiceTest {

    private static final long MOD_ID = 450814997L;

    @Mock(stubOnly = true)
    private PathsFactory pathsFactory;

    @Mock
    private WorkshopModsService modsService;

    @Mock(stubOnly = true)
    private SteamCmdService steamCmdService;

    @Mock(stubOnly = true)
    private ServerInstallationService installationService;

    @Mock(stubOnly = true)
    private ModMetadataService metadataService;

    @Mock(stubOnly = true)
    private SteamCmdItemInfoRepository itemInfoRepository;

    @InjectMocks
    private WorkshopInstallerService installerService;

    @TempDir
    Path tempDir;

    private WorkshopMod mod;
    private SteamCmdJob timedOutJob;

    @BeforeEach
    void setUp() {
        mod = new WorkshopMod(MOD_ID);
        mod.setName("CBA_A3");
        mod.setServerType(ServerType.ARMA3);
        mod.setInstallationStatus(InstallationStatus.INSTALLATION_IN_PROGRESS);

        timedOutJob = new SteamCmdJob(List.of(mod), null);
        timedOutJob.setErrorStatus(ErrorStatus.TIMEOUT);

        when(pathsFactory.getModInstallationPath(MOD_ID, ServerType.ARMA3))
                .thenReturn(tempDir.resolve("mods").resolve(String.valueOf(MOD_ID)));
    }

    @Test
    void persistsResolvedMetadataBeforeStartingSteamCmdDownload() {
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        when(metadataService.fetchModMetadata(List.of(MOD_ID)))
                .thenReturn(Map.of(MOD_ID, new ModMetadata("CBA_A3", "107410")));
        when(installationService.isServerInstalled(ServerType.ARMA3)).thenReturn(true);
        when(steamCmdService.installOrUpdateWorkshopMods(List.of(mod))).thenReturn(new CompletableFuture<>());

        installerService.resolveAndInstall(List.of(mod), false);

        assertThat(mod.getName()).isEqualTo("CBA_A3");
        assertThat(mod.getServerType()).isEqualTo(ServerType.ARMA3);
        verify(modsService).saveMod(mod);
    }

    @Test
    void installsDownloadedModEvenWhenAnotherItemCausedBatchTimeout() throws IOException {
        Path modDirectory = pathsFactory.getModInstallationPath(MOD_ID, ServerType.ARMA3);
        Files.createDirectories(modDirectory);
        when(itemInfoRepository.get(MOD_ID)).thenReturn(Optional.of(new SteamCmdItemInfo(
                MOD_ID, SteamCmdItemInfo.SteamCmdStatus.FINISHED, 100, 1, 1)));

        Path serverPath = tempDir.resolve("server");
        Files.createDirectories(serverPath);
        Path linkPath = serverPath.resolve(mod.getNormalizedName());
        when(installationService.getInstalledRelatedServerTypes(ServerType.ARMA3))
                .thenReturn(List.of(ServerType.ARMA3));
        when(pathsFactory.getModLinkPath(mod.getNormalizedName(), ServerType.ARMA3))
                .thenReturn(linkPath);

        installerService.handleInstallation(mod, timedOutJob);

        assertThat(mod.getInstallationStatus()).isEqualTo(InstallationStatus.FINISHED);
        assertThat(mod.getErrorStatus()).isNull();
        assertThat(linkPath).isSymbolicLink();
        verify(modsService).saveMod(mod);
    }

    @Test
    void marksOnlyMissingModAsTimedOut() {
        when(itemInfoRepository.get(MOD_ID)).thenReturn(Optional.empty());
        installerService.handleInstallation(mod, timedOutJob);

        assertThat(mod.getInstallationStatus()).isEqualTo(InstallationStatus.ERROR);
        assertThat(mod.getErrorStatus()).isEqualTo(ErrorStatus.TIMEOUT);
        verify(modsService).saveMod(mod);
    }

    @Test
    void refreshesExistingModWithoutSteamCmdAndLowercasesFiles() throws IOException {
        mod.setInstallationStatus(InstallationStatus.FINISHED);
        Path modDirectory = pathsFactory.getModInstallationPath(MOD_ID, ServerType.ARMA3);
        Path addonsDirectory = modDirectory.resolve("Addons");
        Files.createDirectories(addonsDirectory);
        Files.createFile(addonsDirectory.resolve("exampleSOG.pbo"));

        Path serverPath = tempDir.resolve("server");
        Files.createDirectories(serverPath);
        Path linkPath = serverPath.resolve(mod.getNormalizedName());
        when(installationService.getInstalledRelatedServerTypes(ServerType.ARMA3))
                .thenReturn(List.of(ServerType.ARMA3));
        when(pathsFactory.getModLinkPath(mod.getNormalizedName(), ServerType.ARMA3))
                .thenReturn(linkPath);

        installerService.refreshInstalledMod(mod);

        assertThat(modDirectory.resolve("addons").resolve("examplesog.pbo")).exists();
        assertThat(linkPath).isSymbolicLink();
        assertThat(mod.getInstallationStatus()).isEqualTo(InstallationStatus.FINISHED);
        verify(modsService).saveMod(mod);
    }

    @Test
    void doesNotTreatExistingDirectoryAsSuccessfulUpdateAfterTimeout() throws IOException {
        Path modDirectory = pathsFactory.getModInstallationPath(MOD_ID, ServerType.ARMA3);
        Files.createDirectories(modDirectory);

        when(itemInfoRepository.get(MOD_ID)).thenReturn(Optional.empty());
        installerService.handleInstallation(mod, timedOutJob);

        assertThat(mod.getInstallationStatus()).isEqualTo(InstallationStatus.ERROR);
        assertThat(mod.getErrorStatus()).isEqualTo(ErrorStatus.TIMEOUT);
        verify(modsService).saveMod(mod);
    }
}
