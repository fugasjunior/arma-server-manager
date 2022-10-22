package cz.forgottenempire.arma3servergui.server.services.impl;

import com.google.common.base.Joiner;
import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.common.util.LogUtils;
import cz.forgottenempire.arma3servergui.server.ServerInstanceInfo;
import cz.forgottenempire.arma3servergui.server.entities.Server;
import cz.forgottenempire.arma3servergui.server.entities.Server.ServerType;
import cz.forgottenempire.arma3servergui.server.exceptions.ModifyingRunningServerException;
import cz.forgottenempire.arma3servergui.server.exceptions.PortAlreadyTakenException;
import cz.forgottenempire.arma3servergui.server.repositories.ServerInstanceInfoRepository;
import cz.forgottenempire.arma3servergui.server.repositories.ServerRepository;
import cz.forgottenempire.arma3servergui.server.services.ArmaServerService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

// TODO way too much responsibilities for this class, separate it into different services
@Service
@Slf4j
public class ArmaServerServiceImpl implements ArmaServerService {

    @Value("${serverDir}")
    private String serverDir;

    @Value("${arma3server.logDir}")
    private String logDir;

    @Value("${betaBranch:#{null}}")
    private String betaBranch;

    @Value("${additionalMods:#{null}}")
    private String[] additionalMods;

    @Value("${arma3server.x64:#{true}}")
    private boolean x64Enabled;

    private final ServerRepository serverRepository;

    private final ServerInstanceInfoRepository instanceInfoRepository;

    private final FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    public ArmaServerServiceImpl(
            ServerRepository serverRepository,
            ServerInstanceInfoRepository instanceInfoRepository,
            FreeMarkerConfigurer freeMarkerConfigurer
    ) {
        this.serverRepository = serverRepository;
        this.instanceInfoRepository = instanceInfoRepository;
        this.freeMarkerConfigurer = freeMarkerConfigurer;

        // Turn off all servers on application shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            instanceInfoRepository.getAll().stream()
                    .map(ServerInstanceInfo::getProcess)
                    .filter(Objects::nonNull)
                    .filter(Process::isAlive)
                    .forEach(Process::destroy);
        }));
    }

    @Override
    public List<Server> getAllServers() {
        return serverRepository.findAll();
    }

    public Optional<Server> getServer(@NotNull Long id) {
        return serverRepository.findById(id);
    }

    @Override
    public Server createServer(Server server) {
        if (server.getType() == ServerType.ARMA3) {
            // Arma 3 server doesn't support customizing Steam query port, it's always game port + 1
            server.setQueryPort(server.getPort() + 1);
        }
        return serverRepository.save(server);
    }

    @Override
    public Server updateServer(Server server) {
        if (isServerInstanceRunning(server)) {
            throw new ModifyingRunningServerException("Cannot modify running server '" + server.getName() + "'");
        }
        return createServer(server);
    }

    @Override
    public void deleteServer(Server server) {
        if (isServerInstanceRunning(server)) {
            throw new ModifyingRunningServerException("Cannot delete running server '" + server.getName() + "'");
        }
        serverRepository.delete(server);
    }

    @Override
    public void startServer(Long id) {
        Server server = serverRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("Server ID " + id + " not found"));

        validatePortsNotTaken(server);

        ServerInstanceInfo instanceInfo = instanceInfoRepository.getServerInstanceInfo(id);
        if (isServerInstanceRunning(instanceInfo)) {
            log.info("Server '{}' (ID {}) is already running", server.getName(), id);
        }

        writeConfig(server);

        Process process = startServerProcess(server);
        instanceInfo = ServerInstanceInfo.builder()
                .id(id)
                .alive(true)
                .startedAt(LocalDateTime.now())
                .maxPlayers(server.getMaxPlayers())
                .process(process)
                .build();
        instanceInfoRepository.storeServerInstanceInfo(id, instanceInfo);
    }

    @Override
    public void shutDownServer(Long id) {
        ServerInstanceInfo instanceInfo = instanceInfoRepository.getServerInstanceInfo(id);
        Process process = instanceInfo.getProcess();
        process.destroy();

        instanceInfo = ServerInstanceInfo.builder()
                .id(id)
                .alive(false)
                .build();
        instanceInfoRepository.storeServerInstanceInfo(id, instanceInfo);
    }

    @Override
    public void restartServer(Long id) {
        shutDownServer(id);
        startServer(id);
    }

    @Override
    public ServerInstanceInfo getServerInstanceInfo(Long id) {
        return instanceInfoRepository.getServerInstanceInfo(id);
    }

//    @Override
//    public synchronized void updateServer(SteamAuth auth) {
//        log.info("Updating server...");
//        serverUpdating = true;
//        shutDownServer();
//
//        new Thread(() -> {
//            SteamCmdParameters parameters = new SteamCmdParameterBuilder()
//                    .withLogin(auth.getUsername(), auth.getPassword(), auth.getSteamGuardToken())
//                    .withInstallDir(serverDir)
//                    .withAppInstall(Constants.STEAM_ARMA3SERVER_ID, true,
//                            betaBranch == null ? "" : " -beta " + betaBranch)
//                    .build();
//            DownloadStatus status = steamCmdWrapper.execute(parameters);
//
//            if (!status.isSuccess()) {
//                log.error("Server update failed due to {}", status.getErrorStatus());
//            }
//            log.info("Server update done");
//            serverUpdating = false;
//        }).start();
//    }

    private void validatePortsNotTaken(Server server) {
        serverRepository.findAllByPortOrQueryPort(server.getPort(), server.getQueryPort()).stream()
                .filter(s -> !s.equals(server))
                .forEach(s -> {
                    ServerInstanceInfo instanceInfo = instanceInfoRepository.getServerInstanceInfo(s.getId());
                    if (instanceInfo.isAlive()) {
                        int conflictingPort = s.getPort() == server.getPort() ?
                                server.getPort()
                                : server.getQueryPort();
                        String errorMessage = String.format("Port conflict: Server '%s' already uses port %d.",
                                s.getName(), conflictingPort);
                        log.error("Server '{}' (ID {}) could not be started because of port conflict (port {})"
                                        + " with server '{}' (ID {})",
                                server.getName(), server.getId(), conflictingPort, s.getName(), s.getId());
                        throw new PortAlreadyTakenException(errorMessage);
                    }
                });
    }

    private boolean isServerInstanceRunning(ServerInstanceInfo instanceInfo) {
        return instanceInfo.isAlive() && instanceInfo.getProcess().isAlive();
    }

    private boolean isServerInstanceRunning(Server server) {
        return isServerInstanceRunning(getServerInstanceInfo(server.getId()));
    }

    private Process startServerProcess(Server server) {
        Process serverProcess = null;

        List<String> parameters = new ArrayList<>();
        parameters.add(getServerExecutable());
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.add("-config=" + getConfigFile(getConfigFileName(server.getId())).getAbsolutePath());
        parameters.add("-port=" + server.getPort());

        // TODO support for serverside mods
        // add enabled mods
        List<String> mods = getActiveModsListAsParameters(server);
        if (!mods.isEmpty()) {
            parameters.addAll(mods);
        }

        // add additional mods from properties
        if (additionalMods != null) {
            Arrays.stream(additionalMods)
                    .map(mod -> "-mod=" + mod)
                    .forEach(parameters::add);
        }

        // add enabled Creator DLCs
        server.getActiveDLCs().stream()
                .map(dlc -> "-mod=" + dlc.getGameId())
                .forEach(parameters::add);

        File logFile = new File(logDir + File.separatorChar + "out_" + server.getId() + ".log");
        LogUtils.prepareLogFile(logFile);

        try {
            log.info("Starting server with options: {}", Joiner.on(" ").join(parameters));

            ProcessBuilder pb = new ProcessBuilder(parameters)
                    .directory(new File(serverDir));

            if (logFile.exists()) {
                pb.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
            }
            serverProcess = pb.start();

            log.info("Server '{}' (ID {}) started (PID {})", server.getName(), server.getId(), serverProcess.pid());
        } catch (IOException e) {
            log.error("Could not start server '{}' (ID {}) due to {}", server.getName(), server.getId(), e.toString());
        }
        return serverProcess;
    }

    private String getServerExecutable() {
        return Path.of(serverDir, x64Enabled ? "arma3server_x64" : "arma3server").toString();
    }

    private void writeConfig(Server server) {
        String configFileName = getConfigFileName(server.getId());
        File configFile = getConfigFile(configFileName);

        // delete old config file
        try {
            log.info("Deleting old configuration '{}'", configFileName);
            FileUtils.forceDelete(configFile);
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            log.error("Could not delete old server config '{}' due to {}", configFile, e.toString());
        }

        // write new config file
        log.info("Writing new server config '{}'", configFileName);
        Template configTemplate;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            configTemplate = freeMarkerConfigurer.getConfiguration()
                    .getTemplate(Constants.TEMPLATE_SERVER_CONFIG_ARMA3);
            configTemplate.process(server, writer);
        } catch (IOException | TemplateException e) {
            log.error("Could not write config template due to {}", e.toString());
        }
    }

    private List<String> getActiveModsListAsParameters(Server server) {
        return server.getActiveMods().stream() // TODO check installation status
                .map(mod -> "-mod=" + mod.getNormalizedName())
                .collect(Collectors.toList());
    }

    private File getConfigFile(String fileName) {
        return new File(serverDir + File.separatorChar + fileName);
    }

    private String getConfigFileName(Long serverId) {
        return "server_" + serverId + ".cfg";
    }
}
