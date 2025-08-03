package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.workshop.SteamAuthDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SteamAuthControllerUnitTest {

    @Mock
    private SteamAuthService steamAuthService;
    @Mock(stubOnly = true)
    private SteamAuthVerifier steamAuthVerifier;

    private SteamAuthController controller;

    @BeforeEach
    void setUp() {
        controller = new SteamAuthController(steamAuthService, steamAuthVerifier);
    }

    @Test
    void whenGetAuthAccount_thenRespondWithAuthAccountWithoutPassword() {
        SteamAuth steamAuth = new SteamAuth(1L, "username", "password", "DEFGH");
        when(steamAuthService.getAuthAccount()).thenReturn(steamAuth);

        ResponseEntity<SteamAuthDto> response = controller.getAuthAccount();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getUsername()).isEqualTo("username");
        assertThat(response.getBody().getSteamGuardToken()).isEqualTo("DEFGH");
        assertThat(response.getBody().getPassword()).isNull();
    }

    @Test
    void whenSetAuthAccount_thenSteamAuthServiceCalled() {
        SteamAuthDto dto = new SteamAuthDto();
        dto.setUsername("username");
        dto.setPassword("password");
        dto.setSteamGuardToken("DEFGH");

        ResponseEntity<?> response = controller.setAuthAccount(dto);

        verify(steamAuthService).setAuthAccount(dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void whenClearAuthAccount_thenAuthServiceClearMethodCalledAndNoContentResponseEntityReturned() {
        ResponseEntity<?> response = controller.clearAuthAccount();

        verify(steamAuthService).clearAuthAccount();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void whenGetAuthStatus_andAuthIsConfigured_thenReturnTrueStatus() {
        when(steamAuthService.isAuthConfigured()).thenReturn(true);

        ResponseEntity<Map<String, Boolean>> response = controller.getAuthStatus();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("isConfigured")).isTrue();
    }

    @Test
    void whenGetAuthStatus_andAuthIsNotConfigured_thenReturnFalseStatus() {
        when(steamAuthService.isAuthConfigured()).thenReturn(false);

        ResponseEntity<Map<String, Boolean>> response = controller.getAuthStatus();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("isConfigured")).isFalse();
    }

    @Test
    void whenVerifyCredentials_andVerificationSucceeds_thenReturnVerificationResult() {
        SteamAuthDto authDto = new SteamAuthDto();
        authDto.setUsername("username");
        authDto.setPassword("password");

        AuthVerificationResult expectedResult = AuthVerificationResult.builder()
                .status(AuthVerificationResult.AuthStatus.SUCCESS)
                .authType(AuthVerificationResult.AuthType.NONE)
                .message("Authentication successful")
                .build();

        when(steamAuthVerifier.verifyCredentials(authDto)).thenReturn(expectedResult);

        ResponseEntity<AuthVerificationResult> response = controller.verifyCredentials(authDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResult);
    }

    @Test
    void whenVerifyCredentials_andVerificationThrowsException_thenReturnErrorResult() {
        SteamAuthDto authDto = new SteamAuthDto();
        authDto.setUsername("username");
        authDto.setPassword("password");

        when(steamAuthVerifier.verifyCredentials(authDto)).thenThrow(new RuntimeException("Test exception"));

        ResponseEntity<AuthVerificationResult> response = controller.verifyCredentials(authDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(AuthVerificationResult.AuthStatus.ERROR);
        assertThat(response.getBody().getMessage()).contains("Test exception");
        assertThat(response.getBody().getAuthType()).isEqualTo(AuthVerificationResult.AuthType.UNKNOWN);
    }
}