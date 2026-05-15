package cz.forgottenempire.servermanager.steamauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.steamcmd.SteamCmdAuthService;
import cz.forgottenempire.servermanager.api.model.SteamAuthDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class SteamAuthVerifierUnitTest {

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String STEAM_GUARD_TOKEN = "ABCDE";
    private static final SteamAuthDto AUTH_DTO = new SteamAuthDto().username(USERNAME).password(PASSWORD).steamGuardToken(STEAM_GUARD_TOKEN);
    private static final SteamAuth EXPECTED_AUTH = new SteamAuth(null, USERNAME, PASSWORD, STEAM_GUARD_TOKEN);

    @Mock(stubOnly = true)
    private SteamCmdAuthService steamCmdAuthService;


    private SteamAuthVerifier verifier;

    @BeforeEach
    void setUp() {
        verifier = new SteamAuthVerifier(steamCmdAuthService);
    }

    @Test
    void whenVerifyCredentials_andVerificationSucceeds_thenReturnSuccessResult() throws IOException, InterruptedException {
        AuthVerificationResult expectedResult = AuthVerificationResult.builder()
                .status(AuthStatus.SUCCESS)
                .authType(AuthType.NONE)
                .message("Authentication successful")
                .build();

        when(steamCmdAuthService.verifyCredentials(EXPECTED_AUTH)).thenReturn(expectedResult);

        AuthVerificationResult result = verifier.verifyCredentials(AUTH_DTO);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void whenVerifyCredentials_andVerificationRequires2FA_thenReturnRequires2FAResult() throws IOException, InterruptedException {
        AuthVerificationResult expectedResult = AuthVerificationResult.builder()
                .status(AuthStatus.REQUIRES_2FA)
                .authType(AuthType.MOBILE)
                .message("Mobile authentication required")
                .build();
                
        when(steamCmdAuthService.verifyCredentials(EXPECTED_AUTH)).thenReturn(expectedResult);

        AuthVerificationResult result = verifier.verifyCredentials(AUTH_DTO);

        assertThat(result).isEqualTo(expectedResult);
    }

    @Test
    void whenVerifyCredentials_andVerificationThrowsException_thenReturnErrorResult() throws IOException, InterruptedException {
        when(steamCmdAuthService.verifyCredentials(EXPECTED_AUTH))
                .thenThrow(new RuntimeException("Test exception"));

        AuthVerificationResult result = verifier.verifyCredentials(AUTH_DTO);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.ERROR);
        assertThat(result.getAuthType()).isEqualTo(AuthType.UNKNOWN);
        assertThat(result.getMessage()).contains("Test exception");
    }
    
    @Test
    void whenVerifyCredentials_andVerificationThrowsIOException_thenReturnErrorResult() throws IOException, InterruptedException {
        when(steamCmdAuthService.verifyCredentials(EXPECTED_AUTH))
                .thenThrow(new IOException("IO exception"));

        AuthVerificationResult result = verifier.verifyCredentials(AUTH_DTO);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.ERROR);
        assertThat(result.getAuthType()).isEqualTo(AuthType.UNKNOWN);
        assertThat(result.getMessage()).contains("IO exception");
    }
}