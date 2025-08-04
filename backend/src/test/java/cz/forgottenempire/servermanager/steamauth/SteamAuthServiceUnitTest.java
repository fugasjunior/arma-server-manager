package cz.forgottenempire.servermanager.steamauth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cz.forgottenempire.servermanager.steamcmd.SteamCmdAuthService;
import cz.forgottenempire.servermanager.workshop.SteamAuthDto;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SteamAuthServiceUnitTest {

    @Mock
    private SteamAuthRepository steamAuthRepository;

    private SteamAuthService service;

    @BeforeEach
    void setUp() {
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

    @Test
    void whenClearAccount_thenRepositoryDeleteAllMethodCalled() {
        service.clearAuthAccount();

        verify(steamAuthRepository).deleteAll();
    }

    @Test
    void whenIsAuthConfiguredAndUsernameAndPasswordAreSet_thenReturnTrue() {
        SteamAuth steamAuth = new SteamAuth(1L, "username", "password", "ABCDE");
        when(steamAuthRepository.findAll()).thenReturn(List.of(steamAuth));

        boolean result = service.isAuthConfigured();

        assertThat(result).isTrue();
    }

    @Test
    void whenIsAuthConfiguredAndUsernameIsNull_thenReturnFalse() {
        SteamAuth steamAuth = new SteamAuth(1L, null, "password", "ABCDE");
        when(steamAuthRepository.findAll()).thenReturn(List.of(steamAuth));

        boolean result = service.isAuthConfigured();

        assertThat(result).isFalse();
    }

    @Test
    void whenIsAuthConfiguredAndPasswordIsNull_thenReturnFalse() {
        SteamAuth steamAuth = new SteamAuth(1L, "username", null, "ABCDE");
        when(steamAuthRepository.findAll()).thenReturn(List.of(steamAuth));

        boolean result = service.isAuthConfigured();

        assertThat(result).isFalse();
    }
}