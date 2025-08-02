package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.workshop.SteamAuthDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/config/auth")
class SteamAuthController {

    private final SteamAuthService authService;
    private final SteamAuthVerifier authVerifier;

    @Autowired
    public SteamAuthController(SteamAuthService authService, SteamAuthVerifier authVerifier) {
        this.authService = authService;
        this.authVerifier = authVerifier;
    }

    @PostMapping
    public ResponseEntity<?> setAuthAccount(@RequestBody SteamAuthDto auth) {
        authService.setAuthAccount(auth);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<SteamAuthDto> getAuthAccount() {
        SteamAuth steamAuth = authService.getAuthAccount();
        SteamAuthDto dto = new SteamAuthDto();
        dto.setUsername(steamAuth.getUsername());
        dto.setSteamGuardToken(steamAuth.getSteamGuardToken());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> clearAuthAccount() {
        authService.clearAuthAccount();
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Checks if Steam authentication is configured
     * @return Status indicating if auth is configured
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Boolean>> getAuthStatus() {
        boolean isConfigured = authService.isAuthConfigured();
        Map<String, Boolean> response = Map.of("isConfigured", isConfigured);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Verifies Steam credentials and detects 2FA requirements
     * @param auth Steam credentials to verify
     * @return Verification result with status, message, and auth type
     */
    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyCredentials(@RequestBody SteamAuthDto auth) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            AuthVerificationResult result = authVerifier.verifyCredentials(auth);
            response.put("status", result.getStatus().toString());
            response.put("message", result.getMessage());
            response.put("authType", result.getAuthType().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", AuthStatus.ERROR.toString());
            response.put("message", "Failed to verify credentials: " + e.getMessage());
            response.put("authType", AuthType.UNKNOWN.toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
