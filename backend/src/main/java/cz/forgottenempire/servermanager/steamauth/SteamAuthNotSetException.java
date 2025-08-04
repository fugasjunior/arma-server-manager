package cz.forgottenempire.servermanager.steamauth;

/**
 * Exception thrown when Steam authentication is not set up
 */
public class SteamAuthNotSetException extends RuntimeException {
    
    public SteamAuthNotSetException() {
        super("Steam authentication is not set up");
    }
    
    public SteamAuthNotSetException(String message) {
        super(message);
    }
}