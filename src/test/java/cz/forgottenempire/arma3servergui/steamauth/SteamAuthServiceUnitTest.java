package cz.forgottenempire.arma3servergui.steamauth;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SteamAuthServiceUnitTest {

    private final SteamAuthRepository steamAuthRepository;
    private final SteamAuthService service;

    public SteamAuthServiceUnitTest() {
        steamAuthRepository = mock(SteamAuthRepository.class);

        service = new SteamAuthService(steamAuthRepository);
    }

    @Test
    void whenGetAuthAccountAndSteamAuthSetup_thenReturnCorrectSteamAuth() {
        SteamAuth steamAuth = new SteamAuth(1L, "username", "hunter2", "ABCDE");
        when(steamAuthRepository.findAll()).thenReturn(List.of(steamAuth));

        SteamAuth authAccount = service.getAuthAccount();

        assertThat(authAccount).isEqualTo(steamAuth);
    }

    @Test
    void whenGetAuthAccountAndNoSteamAuthSetup_thenReturnEmptySteamAuth() {
        when(steamAuthRepository.findAll()).thenReturn(Collections.emptyList());

        SteamAuth authAccount = service.getAuthAccount();

        assertThat(authAccount).isEqualTo(new SteamAuth());
    }
}