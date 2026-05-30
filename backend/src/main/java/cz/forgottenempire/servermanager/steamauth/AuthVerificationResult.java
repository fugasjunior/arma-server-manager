package cz.forgottenempire.servermanager.steamauth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Result of Steam authentication verification process.
 * Contains information about the authentication status, type, and any relevant messages.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthVerificationResult {
    
    private AuthStatus status;
    private String message;
    private AuthType authType;
    
    /**
     * Authentication status
     */
    public enum AuthStatus {
        SUCCESS,
        REQUIRES_2FA,
        INVALID_CREDENTIALS,
        ERROR
    }
    
    /**
     * Type of authentication
     */
    public enum AuthType {
        NONE,
        EMAIL,
        MOBILE,
        UNKNOWN
    }
}