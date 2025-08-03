package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdAuthService;
import cz.forgottenempire.servermanager.workshop.SteamAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for verifying Steam credentials and detecting 2FA requirements
 */
@Service
@Slf4j
public class SteamAuthVerifier {

    private final SteamCmdAuthService steamCmdAuthService;

    @Autowired
    SteamAuthVerifier(SteamCmdAuthService steamCmdAuthService) {
        this.steamCmdAuthService = steamCmdAuthService;
    }

    /**
     * Verifies Steam credentials and detects 2FA requirements
     *
     * @param authDto Steam credentials to verify
     * @return Result of verification with status, message, and auth type
     */
    public AuthVerificationResult verifyCredentials(SteamAuthDto authDto) {
        SteamAuth auth = new SteamAuth();
        auth.setUsername(authDto.getUsername());
        auth.setPassword(authDto.getPassword());
        auth.setSteamGuardToken(authDto.getSteamGuardToken());

        try {
            return steamCmdAuthService.verifyCredentials(auth);
        } catch (Exception e) {
            log.error("Error during credential verification", e);
            return AuthVerificationResult.builder()
                    .status(AuthStatus.ERROR)
                    .authType(AuthType.UNKNOWN)
                    .message("Verification failed: " + e.getMessage())
                    .build();
        }
    }
}