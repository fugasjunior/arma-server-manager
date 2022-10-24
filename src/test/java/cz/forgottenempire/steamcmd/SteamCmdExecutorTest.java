package cz.forgottenempire.steamcmd;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cz.forgottenempire.arma3servergui.steamcmd.SteamCmdExecutor;
import cz.forgottenempire.arma3servergui.steamcmd.entities.SteamCmdParameters;
import cz.forgottenempire.arma3servergui.steamcmd.exceptions.IOOperationException;
import cz.forgottenempire.arma3servergui.steamcmd.exceptions.NoSubscriptionException;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class SteamCmdExecutorTest {

    private static final String STEAMCMD_PATH = "D:\\arma3gui\\steamcmd\\steamcmd.exe";
    private static final String INSTALL_DIR = "D:\\arma3gui\\test\\installdir";
    private static final Long ARMA3_SERVER_ID = 233780L;
    private static final Long ARMA3_GAME_ID = 107410L;
    private static final Long ARMA3_MOD_WORKSHOP_ID = 450814997L; // CBA_A3
    private static final Long ARMA3_LARGE_MOD_WORKSHOP_ID = 735566597L; // Project Opfor (1.7 GB)

    private SteamCmdExecutor steamCmdExecutor;

    @BeforeEach
    void setUp() {
        File steamCmdFile = new File(STEAMCMD_PATH);
        steamCmdExecutor = new SteamCmdExecutor(steamCmdFile);
    }

    @AfterEach
    void tearDown() {
        try {
            FileUtils.deleteDirectory(new File(INSTALL_DIR));
        } catch (IOException e) {
            System.out.println("Could not delete directory!");
        }
    }

    @Test
    void whenAnonymousLogin_noExceptionThrown() {
        SteamCmdParameters params = new SteamCmdParameters.Builder()
                .withAnonymousLogin()
                .build();
        steamCmdExecutor.setParameters(params);

        assertDoesNotThrow(() -> steamCmdExecutor.execute());
    }

    @Test
    void whenAnonymousUserDownloadsArmaServer_NoSubscriptionExceptionThrown() {
        SteamCmdParameters params = new SteamCmdParameters.Builder()
                .withAnonymousLogin()
                .withInstallDir(INSTALL_DIR)
                .withAppInstall(ARMA3_SERVER_ID, true)
                .build();
        steamCmdExecutor.setParameters(params);

        assertThrows(NoSubscriptionException.class, () -> steamCmdExecutor.execute());
    }

    @Test
    void whenAnonymousUserDownloadsArmaMod_noExceptionThrown() {
        SteamCmdParameters params = new SteamCmdParameters.Builder()
                .withAnonymousLogin()
                .withInstallDir(INSTALL_DIR)
                .withWorkshopItemInstall(ARMA3_GAME_ID, ARMA3_MOD_WORKSHOP_ID, true)
                .build();
        steamCmdExecutor.setParameters(params);

        assertDoesNotThrow(() -> steamCmdExecutor.execute());
    }

    @Test
    void whenInvalidInstallDirGiven_IOOperationExceptionThrown() {
        SteamCmdParameters params = new SteamCmdParameters.Builder()
                .withAnonymousLogin()
                .withInstallDir("H:\\NONEXISTING\\DIRECTORY")
                .withWorkshopItemInstall(ARMA3_GAME_ID, ARMA3_MOD_WORKSHOP_ID, true)
                .build();
        steamCmdExecutor.setParameters(params);

        assertThrows(IOOperationException.class, () -> steamCmdExecutor.execute());
    }

    @Test
    @Disabled("Downloads a large mod file, which takes a long time and needs plenty of disk space")
    void whenDownloadsLargeMod_ExceptionNotThrown() {
        SteamCmdParameters params = new SteamCmdParameters.Builder()
                .withAnonymousLogin()
                .withInstallDir(INSTALL_DIR)
                .withWorkshopItemInstall(ARMA3_GAME_ID, ARMA3_LARGE_MOD_WORKSHOP_ID, true)
                .build();
        steamCmdExecutor.setParameters(params);

        assertDoesNotThrow(() -> steamCmdExecutor.execute());
    }

}