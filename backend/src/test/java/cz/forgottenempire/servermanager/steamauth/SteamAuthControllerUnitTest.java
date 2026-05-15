package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.api.model.AuthStatus;
import cz.forgottenempire.servermanager.api.model.AuthType;
import cz.forgottenempire.servermanager.api.model.AuthVerificationResultDto;
import cz.forgottenempire.servermanager.api.model.SteamAuthDto;
import cz.forgottenempire.servermanager.api.model.SteamAuthStatusDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

        ResponseEntity<SteamAuthDto> response = controller.getSteamAuth();

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

        ResponseEntity<?> response = controller.setSteamAuth(dto);

        verify(steamAuthService).setAuthAccount(dto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void whenClearAuthAccount_thenAuthServiceClearMethodCalledAndNoContentResponseEntityReturned() {
        ResponseEntity<?> response = controller.clearSteamAuth();

        verify(steamAuthService).clearAuthAccount();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void whenGetAuthStatus_andAuthIsConfigured_thenReturnTrueStatus() {
        when(steamAuthService.isAuthConfigured()).thenReturn(true);

        ResponseEntity<SteamAuthStatusDto> response = controller.getSteamAuthStatus();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIsConfigured()).isTrue();
    }

    @Test
    void whenGetAuthStatus_andAuthIsNotConfigured_thenReturnFalseStatus() {
        when(steamAuthService.isAuthConfigured()).thenReturn(false);

        ResponseEntity<SteamAuthStatusDto> response = controller.getSteamAuthStatus();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getIsConfigured()).isFalse();
    }

    @Test
    void whenVerifyCredentials_andVerificationSucceeds_thenReturnVerificationResult() {
        SteamAuthDto authDto = new SteamAuthDto();
        authDto.setUsername("username");
        authDto.setPassword("password");

        AuthVerificationResult verificationResult = AuthVerificationResult.builder()
                .status(AuthVerificationResult.AuthStatus.SUCCESS)
                .authType(AuthVerificationResult.AuthType.NONE)
                .message("Authentication successful")
                .build();

        when(steamAuthVerifier.verifyCredentials(authDto)).thenReturn(verificationResult);

        ResponseEntity<AuthVerificationResultDto> response = controller.verifySteamAuth(authDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(AuthStatus.SUCCESS);
        assertThat(response.getBody().getAuthType()).isEqualTo(AuthType.NONE);
        assertThat(response.getBody().getMessage()).isEqualTo("Authentication successful");
    }

    @Test
    void whenVerifyCredentials_andVerificationThrowsException_thenReturnErrorResult() {
        SteamAuthDto authDto = new SteamAuthDto();
        authDto.setUsername("username");
        authDto.setPassword("password");

        when(steamAuthVerifier.verifyCredentials(authDto)).thenThrow(new RuntimeException("Test exception"));

        ResponseEntity<AuthVerificationResultDto> response = controller.verifySteamAuth(authDto);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(AuthStatus.ERROR);
        assertThat(response.getBody().getMessage()).contains("Test exception");
        assertThat(response.getBody().getAuthType()).isEqualTo(AuthType.UNKNOWN);
    }
}