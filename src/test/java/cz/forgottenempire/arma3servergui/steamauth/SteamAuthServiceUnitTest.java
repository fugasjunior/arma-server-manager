package cz.forgottenempire.arma3servergui.steamauth;

import cz.forgottenempire.arma3servergui.workshop.SteamAuthDto;
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

    @Test
    void whenSetSteamAccountWithNewPassword_thenPasswordIsUpdated() {
        SteamAuth prevAuth = new SteamAuth(1L, "username", "hunter2", "ABCDE");
        when(steamAuthRepository.findAll()).thenReturn(List.of(prevAuth));
        SteamAuthDto newAuth = new SteamAuthDto();
        newAuth.setUsername("new_username");
        newAuth.setPassword("n3wp4ssw0rd");
        newAuth.setSteamGuardToken("FGHIJ");

        service.setAuthAccount(newAuth);

        SteamAuth actualAuthAccount = service.getAuthAccount();
        assertThat(actualAuthAccount.getUsername()).isEqualTo("new_username");
        assertThat(actualAuthAccount.getPassword()).isEqualTo("n3wp4ssw0rd");
        assertThat(actualAuthAccount.getSteamGuardToken()).isEqualTo("FGHIJ");
    }

    @Test
    void whenSetSteamAccountWithoutPassword_thenOriginalPasswordIsKept() {
        SteamAuth prevAuth = new SteamAuth(1L, "username", "hunter2", "ABCDE");
        when(steamAuthRepository.findAll()).thenReturn(List.of(prevAuth));
        SteamAuthDto newAuth = new SteamAuthDto();
        newAuth.setUsername("new_username");
        newAuth.setSteamGuardToken("FGHIJ");

        service.setAuthAccount(newAuth);

        SteamAuth actualAuthAccount = service.getAuthAccount();
        assertThat(actualAuthAccount.getUsername()).isEqualTo("new_username");
        assertThat(actualAuthAccount.getPassword()).isEqualTo("hunter2");
        assertThat(actualAuthAccount.getSteamGuardToken()).isEqualTo("FGHIJ");
    }
}