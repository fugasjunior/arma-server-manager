package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdAuthService;
import cz.forgottenempire.servermanager.api.model.SteamAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
class SteamCmdSteamAuthVerifier implements SteamAuthVerifier {

    private final SteamCmdAuthService steamCmdAuthService;

    @Autowired
    SteamCmdSteamAuthVerifier(SteamCmdAuthService steamCmdAuthService) {
        this.steamCmdAuthService = steamCmdAuthService;
    }

    @Override
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
