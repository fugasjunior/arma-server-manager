package cz.forgottenempire.servermanager.installation;

import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ProcessFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.util.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
class TestRunService {

    private static final String LOCALHOST = "localhost";

    private final ProcessFactory processFactory;
    private final PathsFactory pathsFactory;

    @Autowired
    public TestRunService(
            ProcessFactory processFactory, PathsFactory pathsFactory) {
        this.processFactory = processFactory;
        this.pathsFactory = pathsFactory;
    }

    public synchronized void performServerDryRun(ServerInstallation serverInstallation) {

        int port = findAvailablePort();
        int queryPort = port + 1;

        ServerType type = serverInstallation.getType();

        File configFile;
        Process serverProcess;
        try {
            configFile = createTestConfigFile(type, port, queryPort);
            serverProcess = startServerForDryRun(type, port, configFile);
        } catch (IOException e) {
            log.error("Error when launching server executable", e);
            throw new RuntimeException(e);
        }

        int attempts = 0;
        while (attempts < 10) {
            try (SourceQueryClient sourceQueryClient = new SourceQueryClient()) {
                InetSocketAddress serverAddress = new InetSocketAddress(LOCALHOST, queryPort);
                SourceServer queryServerInfo = sourceQueryClient.getServerInfo(serverAddress).get(30, TimeUnit.SECONDS);
                serverInstallation.setVersion(queryServerInfo.getGameVersion());
                break;
            } catch (Exception e) {
                if (!serverProcess.isAlive()) {
                    log.error("Server crashed before it could be queried");
                    throw new RuntimeException(e);
                }
                // no matter of the timeout set for CompletableFuture#get(), the several first query requests fail
                // automatically after 5 seconds. That's why this ugly loop for retrying is needed.
                attempts++;
                if (attempts == 10) {
                    serverProcess.destroy();
                    throw new RuntimeException(e);
                }
            }
        }
        serverProcess.destroy();
        configFile.delete();
    }

    private Process startServerForDryRun(ServerType type, int port, File configFile) throws IOException {
        List<String> parameters = getLaunchParameters(type, port, configFile);
        File executable = pathsFactory.getServerExecutableWithFallback(type);
        log.info("Starting server '{}' for dry run with parameters: {}", type, parameters);
        return processFactory.startProcessWithDiscardedOutput(executable, parameters);
    }

    private File createTestConfigFile(ServerType type, int port, int queryPort) throws IOException {
        File testCfgFile = Path.of(pathsFactory.getServerPath(type).toString(), "TEST_CONFIG.cfg").toFile();
        if (testCfgFile.isFile()) {
            testCfgFile.delete();
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(testCfgFile))) {
            String config = "";
            if (type == ServerType.ARMA3) {
                config = getArma3TestConfig();
            } else if (type == ServerType.DAYZ || type == ServerType.DAYZ_EXP) {
                config = getDayZTestConfig(queryPort);
            } else if (type == ServerType.REFORGER) {
                config = getReforgerTestConfig(port, queryPort);
            }
            writer.write(config);
        }
        return testCfgFile;
    }

    private List<String> getLaunchParameters(ServerType type, int port, File configFile) {
        List<String> parameters = new ArrayList<>();
        if (type == ServerType.ARMA3) {
            addArma3LaunchParameters(parameters, port);
        } else if (type == ServerType.DAYZ || type == ServerType.DAYZ_EXP) {
            addDayZLaunchParameters(parameters, port);
        } else if (type == ServerType.REFORGER) {
            addReforgerLaunchParameters(parameters, configFile.getAbsolutePath());
        }
        return parameters;
    }

    private void addArma3LaunchParameters(List<String> parameters, int port) {
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.add("-config=TEST_CONFIG.cfg");
        parameters.add("-port=" + port);
    }

    private void addDayZLaunchParameters(List<String> parameters, int port) {
        parameters.add("-limitFPS=30");
        parameters.add("-freezeCheck");
        parameters.add("-config=TEST_CONFIG.cfg");
        parameters.add("-port=" + port);
    }

    private void addReforgerLaunchParameters(List<String> parameters, String configFilePath) {
        parameters.add("-config");
        parameters.add(configFilePath);
        parameters.add("-backendlog");
        parameters.add("-nothrow");
        parameters.add("-maxFPS=30");
    }

    private String getArma3TestConfig() {
        return """
                hostName="TEST SERVER"
                """;
    }

    private String getDayZTestConfig(int queryPort) {
        return """
                hostName="TEST SERVER"
                instanceId=9999
                steamQueryPort=%d
                """.formatted(queryPort);
    }

    private String getReforgerTestConfig(int port, int queryPort) {
        return """
                {
                	"bindAddress": "",
                	"bindPort": %d,
                	"publicAddress": "",
                	"publicPort": %d,
                	"a2s": {
                		"address": "127.0.0.1",
                		"port": %d
                	},
                	"game": {
                		"name": "TEST SERVER",
                		"scenarioId": "{ECC61978EDCC2B5A}Missions/23_Campaign.conf",
                		"visible": false
                	}
                }
                 """.formatted(port, port, queryPort);
    }

    private int findAvailablePort() {
        int port = 3000;
        while (!SystemUtils.isPortAvailable(port) || !SystemUtils.isPortAvailable(port + 1)) {
            port += 2;
        }
        return port;
    }
}