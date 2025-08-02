package cz.forgottenempire.servermanager.steamcmd;

import com.google.common.io.Files;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthStatus;
import cz.forgottenempire.servermanager.steamauth.AuthVerificationResult.AuthType;
import cz.forgottenempire.servermanager.steamauth.SteamAuth;
import cz.forgottenempire.servermanager.steamcmd.SteamAuthNotSetException;
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

    private static final String STEAM_CREDENTIALS_PLACEHOLDER = "<{STEAM_CREDENTIALS_PLACEHOLDER}>";
    private final ProcessFactory processFactory;
    private final File steamCmdFile;

    @Autowired
    public SteamCmdAuthService(
            ProcessFactory processFactory,
            @Value("${steamcmd.path}") String steamCmdFilePath) {
        this.processFactory = processFactory;
        this.steamCmdFile = new File(steamCmdFilePath);
        if (!steamCmdFile.exists()) {
            throw new IllegalStateException("Invalid path to SteamCMD executable given");
        }
    }

    /**
     * Executes a SteamCMD command with the provided parameters and authentication.
     * @param parameters SteamCMD parameters to execute
     * @param auth Steam authentication credentials
     * @return Output from SteamCMD execution
     * @throws IOException If an I/O error occurs
     * @throws InterruptedException If the process is interrupted
     */
    public String executeSteamCmd(SteamCmdParameters parameters, SteamAuth auth) 
            throws IOException, InterruptedException {
        List<String> commands = getCommands(parameters, auth);

        FileUtils.deleteQuietly(new File(steamCmdFile.getParent() + "/config/config.vdf"));

        Process process = processFactory.startProcessWithUnbufferedOutput(steamCmdFile, commands);
        
        // Capture the output
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String output = reader.lines().collect(Collectors.joining("\n"));
        
        // Wait for process to complete with timeout
        if (!process.waitFor(30, TimeUnit.SECONDS)) {
            process.destroyForcibly();
            throw new RuntimeException("SteamCMD execution timed out");
        }
        
        return output;
    }

    /**
     * Analyzes SteamCMD output to determine authentication status and type.
     * @param output Output from SteamCMD execution
     * @return Result of verification with status, message, and auth type
     */
    public AuthVerificationResult analyzeSteamCmdOutput(String output) {
        String lowerOutput = output.toLowerCase();
        
        // Check for successful login
        if (lowerOutput.contains("success") && !lowerOutput.contains("failed")) {
            return AuthVerificationResult.builder()
                    .status(AuthStatus.SUCCESS)
                    .authType(AuthType.NONE)
                    .message("Login successful")
                    .build();
        }
        
        // Check for 2FA requirements
        if (lowerOutput.contains("steam guard") && !lowerOutput.contains("steam guard code provided")) {
            // Determine 2FA type
            if (lowerOutput.contains("check your email")) {
                return AuthVerificationResult.builder()
                        .status(AuthStatus.REQUIRES_2FA)
                        .authType(AuthType.EMAIL)
                        .message("Steam Guard code required. Please check your email.")
                        .build();
            } else if (lowerOutput.contains("mobile authenticator")) {
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
        
        // Check for invalid credentials
        if (lowerOutput.contains("invalid login") || lowerOutput.contains("incorrect password")) {
            return AuthVerificationResult.builder()
                    .status(AuthStatus.INVALID_CREDENTIALS)
                    .authType(AuthType.NONE)
                    .message("Invalid username or password.")
                    .build();
        }
        
        // Default to error
        return AuthVerificationResult.builder()
                .status(AuthStatus.ERROR)
                .authType(AuthType.UNKNOWN)
                .message("Unknown error occurred during verification.")
                .build();
    }

    private List<String> getCommands(SteamCmdParameters parameters, SteamAuth auth) {
        List<String> commands = new ArrayList<>();
        parameters.get().forEach(parameter -> {
            if (parameter.contains(STEAM_CREDENTIALS_PLACEHOLDER)) {
                commands.add(parameter.replace(STEAM_CREDENTIALS_PLACEHOLDER, getAuthString(auth)));
            } else {
                commands.add(parameter);
            }
        });
        commands.add("+quit");
        return commands;
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