package cz.forgottenempire.servermanager.steamcmd;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.steamauth.SteamAuth;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

@ExtendWith(MockitoExtension.class)
class SteamCmdAuthServiceUnitTest {

    private static final long ID = 1L;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    @Mock(stubOnly = true)
    private ProcessFactory processFactory;

    @Mock(stubOnly = true)
    private PathsFactory pathsFactory;

    @Mock(stubOnly = true)
    private File mockSteamCmdFile;

    @Mock(stubOnly = true)
    private File mockSteamCmdCacheFile;

    @Mock(stubOnly = true)
    private Process mockProcess;

    private SteamCmdAuthService service;

    @BeforeEach
    void setUp() {
        lenient().when(pathsFactory.getSteamCmdExecutable()).thenReturn(mockSteamCmdFile);
        lenient().when(pathsFactory.getSteamCmdCacheFile()).thenReturn(mockSteamCmdCacheFile);

        service = new SteamCmdAuthService(processFactory, pathsFactory);
    }

    @Test
    void whenVerifyCredentials_withSuccessfulLogin_thenReturnSuccessResult() throws IOException, InterruptedException {
        SteamAuth auth = new SteamAuth(ID, USERNAME, PASSWORD, null);
        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(
                """
                        Connecting anonymously to Steam Public...Logged in OK
                        Logging in user 'username' to Steam Public...OK
                        Connected to Steam servers""".getBytes()));
        when(mockProcess.waitFor(any(Long.class), any())).thenReturn(true);
        when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList())).thenReturn(mockProcess);

        AuthVerificationResult result = service.verifyCredentials(auth);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.SUCCESS);
        assertThat(result.getAuthType()).isEqualTo(AuthType.NONE);
        assertThat(result.getMessage()).isEqualTo("Login successful.");
    }

    @Test
    void whenVerifyCredentials_withInvalidPassword_thenReturnInvalidCredentialsResult() throws IOException, InterruptedException {
        SteamAuth auth = new SteamAuth(ID, USERNAME, "wrong_password", null);

        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(
                """
                        Connecting anonymously to Steam Public...Logged in OK
                        Logging in user 'username' to Steam Public...FAILED invalid password""".getBytes()));
        when(mockProcess.waitFor(any(Long.class), any())).thenReturn(true);
        when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList())).thenReturn(mockProcess);

        AuthVerificationResult result = service.verifyCredentials(auth);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.INVALID_CREDENTIALS);
        assertThat(result.getAuthType()).isEqualTo(AuthType.NONE);
        assertThat(result.getMessage()).isEqualTo("Invalid username or password.");
    }

    @Test
    void whenVerifyCredentials_withInvalidSteamGuardCode_thenReturnInvalidCredentialsResult() throws IOException, InterruptedException {
        SteamAuth auth = new SteamAuth(ID, USERNAME, PASSWORD, "INVALID");

        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(
                """
                        Connecting anonymously to Steam Public...Logged in OK
                        Logging in user 'username' to Steam Public...FAILED Steam Guard code was invalid""".getBytes()));
        when(mockProcess.waitFor(any(Long.class), any())).thenReturn(true);
        when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList())).thenReturn(mockProcess);

        AuthVerificationResult result = service.verifyCredentials(auth);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.INVALID_CREDENTIALS);
        assertThat(result.getAuthType()).isEqualTo(AuthType.EMAIL);
        assertThat(result.getMessage()).isEqualTo("Invalid Steam Guard code.");
    }

    @Test
    void whenVerifyCredentials_withEmailAuthRequired_thenReturnRequires2FAResult() throws IOException, InterruptedException {
        SteamAuth auth = new SteamAuth(ID, USERNAME, PASSWORD, null);

        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(
                """
                        Connecting anonymously to Steam Public...Logged in OK
                        Logging in user 'username' to Steam Public...FAILED Steam Guard: This account is protected by Steam Guard. Check your email for the message from Steam with the Steam Guard code."""
                        .getBytes()));
        when(mockProcess.waitFor(any(Long.class), any())).thenReturn(true);
        when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList())).thenReturn(mockProcess);

        AuthVerificationResult result = service.verifyCredentials(auth);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.REQUIRES_2FA);
        assertThat(result.getAuthType()).isEqualTo(AuthType.EMAIL);
        assertThat(result.getMessage()).isEqualTo("Steam Guard code required. Please check your email.");
    }

    @Test
    void whenVerifyCredentials_withMobileAuthRequired_thenReturnRequires2FAResult() throws IOException, InterruptedException {
        SteamAuth auth = new SteamAuth(ID, USERNAME, PASSWORD, null);

        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(
                """
                        Connecting anonymously to Steam Public...Logged in OK
                        Logging in user 'username' to Steam Public...FAILED Steam Guard: This account is using the Steam mobile authenticator."""
                        .getBytes()));
        when(mockProcess.waitFor(any(Long.class), any())).thenReturn(true);
        when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList())).thenReturn(mockProcess);

        AuthVerificationResult result = service.verifyCredentials(auth);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.REQUIRES_2FA);
        assertThat(result.getAuthType()).isEqualTo(AuthType.MOBILE);
        assertThat(result.getMessage()).isEqualTo("Mobile authenticator detected. This form of authentication is not supported.");
    }

    @Test
    void whenVerifyCredentials_withUnknown2FAMethod_thenReturnUnknownResult() throws IOException, InterruptedException {
        SteamAuth auth = new SteamAuth(ID, USERNAME, PASSWORD, null);

        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(
                """
                        Connecting anonymously to Steam Public...Logged in OK
                        Logging in user 'username' to Steam Public...FAILED Steam Guard: This account is using the Unknown authenticator."""
                        .getBytes()));
        when(mockProcess.waitFor(any(Long.class), any())).thenReturn(true);
        when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList())).thenReturn(mockProcess);

        AuthVerificationResult result = service.verifyCredentials(auth);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.REQUIRES_2FA);
        assertThat(result.getAuthType()).isEqualTo(AuthType.UNKNOWN);
        assertThat(result.getMessage()).isEqualTo("Unknown 2FA type detected.");
    }

    @Test
    void whenVerifyCredentials_withTooManyLoginAttempts_thenReturnInvalidCredentialsResult() throws IOException, InterruptedException {
        SteamAuth auth = new SteamAuth(ID, USERNAME, PASSWORD, null);

        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(
                """
                        Connecting anonymously to Steam Public...Logged in OK
                        Logging in user 'username' to Steam Public...FAILED rate limit exceeded""".getBytes()));
        when(mockProcess.waitFor(any(Long.class), any())).thenReturn(true);
        when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList())).thenReturn(mockProcess);

        AuthVerificationResult result = service.verifyCredentials(auth);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.INVALID_CREDENTIALS);
        assertThat(result.getAuthType()).isEqualTo(AuthType.NONE);
        assertThat(result.getMessage()).isEqualTo("Too many login attempts. Please try again later.");
    }

    @Test
    void whenVerifyCredentials_withUnknownError_thenReturnErrorResult() throws IOException, InterruptedException {
        SteamAuth auth = new SteamAuth(ID, USERNAME, PASSWORD, null);

        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream(
                """
                        Connecting anonymously to Steam Public...Logged in OK
                        Logging in user 'username' to Steam Public...FAILED unknown error""".getBytes()));
        when(mockProcess.waitFor(any(Long.class), any())).thenReturn(true);
        when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList())).thenReturn(mockProcess);

        AuthVerificationResult result = service.verifyCredentials(auth);

        assertThat(result.getStatus()).isEqualTo(AuthStatus.ERROR);
        assertThat(result.getAuthType()).isEqualTo(AuthType.UNKNOWN);
        assertThat(result.getMessage()).isEqualTo("Unknown error occurred during verification.");
    }

    @Test
    void whenVerifyCredentials_withProcessTimeout_thenThrowRuntimeException() throws IOException, InterruptedException {
        SteamAuth auth = new SteamAuth(ID, USERNAME, PASSWORD, null);

        when(mockProcess.getInputStream()).thenReturn(new ByteArrayInputStream("".getBytes()));
        when(mockProcess.waitFor(any(Long.class), any())).thenReturn(false);
        when(processFactory.startProcessWithUnbufferedOutput(any(File.class), anyList())).thenReturn(mockProcess);

        assertThatThrownBy(() -> service.verifyCredentials(auth))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("SteamCMD execution timed out");
    }

    @Test
    void whenVerifyCredentials_withNullUsername_thenThrowSteamAuthNotSetException() {
        SteamAuth auth = new SteamAuth(ID, null, "password", null);

        assertThatThrownBy(() -> service.verifyCredentials(auth))
                .isInstanceOf(SteamAuthNotSetException.class);
    }

    @Test
    void whenVerifyCredentials_withNullPassword_thenThrowSteamAuthNotSetException() {
        SteamAuth auth = new SteamAuth(ID, "username", null, null);

        assertThatThrownBy(() -> service.verifyCredentials(auth))
                .isInstanceOf(SteamAuthNotSetException.class);
    }
}
