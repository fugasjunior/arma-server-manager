package cz.forgottenempire.servermanager.steamauth;

import cz.forgottenempire.servermanager.workshop.SteamAuthDto;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SteamAuthControllerUnitTest {

    private final SteamAuthService steamAuthService;
    private final SteamAuthController controller;

    public SteamAuthControllerUnitTest() {
        steamAuthService = mock(SteamAuthService.class);
        controller = new SteamAuthController(steamAuthService);
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
}