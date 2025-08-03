package cz.forgottenempire.servermanager.steamcmd;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.steamauth.SteamAuth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Service for executing SteamCMD commands related to authentication and verification.
 */
@Service
@Slf4j
public class SteamCmdAuthService {

    public static final int TIMEOUT_SECONDS = 30;
    private final ProcessFactory processFactory;
    private final PathsFactory pathsFactory;

    @Autowired
    public SteamCmdAuthService(
            ProcessFactory processFactory,
            PathsFactory pathsFactory) {
        this.processFactory = processFactory;
        this.pathsFactory = pathsFactory;
    }

    /**
     * Executes a SteamCMD command with the provided parameters and authentication.
     * @param auth Steam authentication credentials
     * @return Output from SteamCMD execution
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the process is interrupted
     */
    public AuthVerificationResult verifyCredentials(SteamAuth auth)
            throws IOException, InterruptedException {
        deleteCachedCredentials();
        String output = attemptSteamCmdLogin(auth);
        return analyzeSteamCmdOutput(output);
    }

    private void deleteCachedCredentials() {
        File steamCmdFile = pathsFactory.getSteamCmdExecutable();
        FileUtils.deleteQuietly(new File(steamCmdFile.getParent() + "/config/config.vdf"));
    }

    private String attemptSteamCmdLogin(SteamAuth auth) throws IOException, InterruptedException {
        List<String> commands = prepareSteamCmdLoginCommands(auth);
        File steamCmdFile = pathsFactory.getSteamCmdExecutable();
        Process process = processFactory.startProcessWithUnbufferedOutput(steamCmdFile, commands);

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = reader.lines().collect(Collectors.joining("\n"));

        if (!process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new RuntimeException("SteamCMD execution timed out");
        }

        return output;
    }

    private List<String> prepareSteamCmdLoginCommands(SteamAuth auth) {
        SteamCmdParameters parameters = new SteamCmdParameters.Builder()
                .withLogin()
                .build();

        List<String> commands = new ArrayList<>();
        parameters.get().forEach(parameter -> {
            if (parameter.contains(SteamCmdParameters.STEAM_CREDENTIALS_PLACEHOLDER)) {
                commands.add(parameter.replace(SteamCmdParameters.STEAM_CREDENTIALS_PLACEHOLDER, getAuthString(auth)));
            } else {
                commands.add(parameter);
            }
        });
        return commands;
    }

    /**
     * Analyzes SteamCMD output to determine authentication status and type.
     * @param output Output from SteamCMD execution
     * @return Result of verification with status, message, and auth type
     */
    private AuthVerificationResult analyzeSteamCmdOutput(String output) {
        String lowerOutput = output.toLowerCase();

        if (isLoginSuccessful(lowerOutput)) {
            return AuthVerificationResult.builder()
                    .status(AuthStatus.SUCCESS)
                    .authType(AuthType.NONE)
                    .message("Login successful.")
                    .build();
        }

        if (isSteamGuardCodeInvalid(lowerOutput)) {
            return AuthVerificationResult.builder()
                    .status(AuthStatus.INVALID_CREDENTIALS)
                    .authType(AuthType.EMAIL)
                    .message("Invalid Steam Guard code.")
                    .build();
        }

        if (isInvalidPassword(lowerOutput)) {
            return AuthVerificationResult.builder()
                    .status(AuthStatus.INVALID_CREDENTIALS)
                    .authType(AuthType.NONE)
                    .message("Invalid username or password.")
                    .build();
        }

        if (contains2FAInfo(lowerOutput)) {
            return determine2FAtype(lowerOutput);
        }

        if (isTooManyLoginAttempts(lowerOutput)) {
            return AuthVerificationResult.builder()
                    .status(AuthStatus.INVALID_CREDENTIALS)
                    .authType(AuthType.NONE)
                    .message("Too many login attempts. Please try again later.")
                    .build();
        }

        logOutputAsError(output);

        return AuthVerificationResult.builder()
                .status(AuthStatus.ERROR)
                .authType(AuthType.UNKNOWN)
                .message("Unknown error occurred during verification.")
                .build();
    }

    private static void logOutputAsError(String output) {
        String errorMessage = """
                ======== SteamCMD ERROR OUTPUT START ========
                %s
                "======== SteamCMD ERROR OUTPUT END ========
                """.formatted(output);
        log.error(errorMessage);
    }

    private static boolean isLoginSuccessful(String lowerOutput) {
        return lowerOutput.contains("to steam public...ok");
    }

    private static boolean isSteamGuardCodeInvalid(String lowerOutput) {
        return lowerOutput.contains("steam guard code was invalid");
    }

    private static boolean isInvalidPassword(String lowerOutput) {
        return lowerOutput.contains("invalid password");
    }

    private static boolean isTooManyLoginAttempts(String lowerOutput) {
        return lowerOutput.contains("rate limit exceeded");
    }

    private static boolean contains2FAInfo(String lowerOutput) {
        return lowerOutput.contains("steam guard") && !lowerOutput.contains("steam guard code provided");
    }

    private static AuthVerificationResult determine2FAtype(String lowerOutput) {
        if (isEmailAuthenticator(lowerOutput)) {
            return AuthVerificationResult.builder()
                    .status(AuthStatus.REQUIRES_2FA)
                    .authType(AuthType.EMAIL)
                    .message("Steam Guard code required. Please check your email.")
                    .build();
        } else if (isMobileAuthenticator(lowerOutput)) {
            return AuthVerificationResult.builder()
                    .status(AuthStatus.REQUIRES_2FA)
                    .authType(AuthType.MOBILE)
                    .message("Mobile authenticator detected. This form of authentication is not supported.")
                    .build();
        } else {
            return AuthVerificationResult.builder()
                    .status(AuthStatus.REQUIRES_2FA)
                    .authType(AuthType.UNKNOWN)
                    .message("Unknown 2FA type detected.")
                    .build();
        }
    }

    private static boolean isEmailAuthenticator(String lowerOutput) {
        return lowerOutput.contains("check your email for the message");
    }

    private static boolean isMobileAuthenticator(String lowerOutput) {
        return lowerOutput.contains("mobile authenticator");
    }

    private String getAuthString(SteamAuth auth) {
        if (auth.getUsername() == null || auth.getPassword() == null) {
            throw new SteamAuthNotSetException();
        }

        String authString = auth.getUsername() + " " + auth.getPassword();
        if (!StringUtils.isBlank(auth.getSteamGuardToken())) {
            authString += " " + auth.getSteamGuardToken();
        }
        return authString;
    }
}