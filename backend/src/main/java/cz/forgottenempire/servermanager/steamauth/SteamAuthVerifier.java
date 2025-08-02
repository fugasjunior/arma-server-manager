package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdAuthService;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdParameters;
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

    private final SteamAuthRepository authRepository;
    private final SteamCmdAuthService steamCmdAuthService;

    @Autowired
    SteamAuthVerifier(
            SteamAuthRepository authRepository,
            SteamCmdAuthService steamCmdAuthService) {
        this.authRepository = authRepository;
        this.steamCmdAuthService = steamCmdAuthService;
    }

    /**
     * Verifies Steam credentials and detects 2FA requirements
     * @param auth Steam credentials to verify
     * @return Result of verification with status, message, and auth type
     */
    public AuthVerificationResult verifyCredentials(SteamAuthDto auth) {
        // Create a temporary SteamAuth object for this verification
        SteamAuth tempAuth = new SteamAuth();
        tempAuth.setUsername(auth.getUsername());
        tempAuth.setPassword(auth.getPassword());
        tempAuth.setSteamGuardToken(auth.getSteamGuardToken());
        
        // Store the temporary auth for the duration of this verification
        SteamAuth originalAuth = authRepository.findAll().stream().findFirst().orElse(new SteamAuth());
        try {
            // Replace the auth temporarily
            authRepository.deleteAll();
            authRepository.save(tempAuth);
            
            // Create login parameters
            SteamCmdParameters parameters = new SteamCmdParameters.Builder()
                    .withLogin()
                    .build();
            
            // Execute SteamCMD with login command and capture output
            return steamCmdAuthService.verifyCredentials(parameters, tempAuth);
        } catch (Exception e) {
            log.error("Error during credential verification", e);
            return AuthVerificationResult.builder()
                    .status(AuthStatus.ERROR)
                    .authType(AuthType.UNKNOWN)
                    .message("Verification failed: " + e.getMessage())
                    .build();
        } finally {
            // Restore the original auth
            authRepository.deleteAll();
            if (originalAuth.getId() != null) {
                authRepository.save(originalAuth);
            }
        }
    }
}