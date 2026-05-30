package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.api.SteamAuthApi;
import cz.forgottenempire.servermanager.api.model.AuthStatus;
import cz.forgottenempire.servermanager.api.model.AuthType;
import cz.forgottenempire.servermanager.api.model.AuthVerificationResultDto;
import cz.forgottenempire.servermanager.api.model.SteamAuthDto;
import cz.forgottenempire.servermanager.api.model.SteamAuthStatusDto;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAuthority('" + PermissionCode.STEAM_AUTH_ADMIN + "')")
public class SteamAuthController implements SteamAuthApi {

    private final SteamAuthService authService;
    private final SteamAuthVerifier authVerifier;

    @Autowired
    public SteamAuthController(SteamAuthService authService, SteamAuthVerifier authVerifier) {
        this.authService = authService;
        this.authVerifier = authVerifier;
    }

    @Override
    public ResponseEntity<Void> setSteamAuth(SteamAuthDto steamAuthDto) {
        authService.setAuthAccount(steamAuthDto);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<SteamAuthDto> getSteamAuth() {
        SteamAuth steamAuth = authService.getAuthAccount();
        SteamAuthDto dto = new SteamAuthDto()
                .username(steamAuth.getUsername())
                .steamGuardToken(steamAuth.getSteamGuardToken());
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<Void> clearSteamAuth() {
        authService.clearAuthAccount();
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<SteamAuthStatusDto> getSteamAuthStatus() {
        boolean isConfigured = authService.isAuthConfigured();
        return ResponseEntity.ok(new SteamAuthStatusDto().isConfigured(isConfigured));
    }

    @Override
    public ResponseEntity<AuthVerificationResultDto> verifySteamAuth(SteamAuthDto steamAuthDto) {
        try {
            AuthVerificationResult result = authVerifier.verifyCredentials(steamAuthDto);
            AuthVerificationResultDto dto = new AuthVerificationResultDto()
                    .status(AuthStatus.fromValue(result.getStatus().name()))
                    .message(result.getMessage())
                    .authType(AuthType.fromValue(result.getAuthType().name()));
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            AuthVerificationResultDto dto = new AuthVerificationResultDto()
                    .status(AuthStatus.ERROR)
                    .message("Failed to verify credentials: " + e.getMessage())
                    .authType(AuthType.UNKNOWN);
            return ResponseEntity.internalServerError().body(dto);
        }
    }
}
