package cz.forgottenempire.arma3servergui.serverinstance;

import com.google.common.base.Joiner;
import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.common.PathsFactory;
import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.serverinstance.entities.Arma3Server;
import cz.forgottenempire.arma3servergui.serverinstance.entities.DayZServer;
import cz.forgottenempire.arma3servergui.serverinstance.entities.Server;
import cz.forgottenempire.arma3servergui.serverinstance.exceptions.ModifyingRunningServerException;
import cz.forgottenempire.arma3servergui.serverinstance.exceptions.PortAlreadyTakenException;
import cz.forgottenempire.arma3servergui.util.LogUtils;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
class ServerInstanceServiceImpl implements ServerInstanceService {

    private final ServerRepository serverRepository;
    private final ServerInstanceInfoRepository instanceInfoRepository;
    private final FreeMarkerConfigurer freeMarkerConfigurer;
    private final PathsFactory pathsFactory;
    @Value("${arma3server.logDir}")
    private String logDir; // TODO get rid of this, fix for multiple server instances
    @Value("${additionalMods:#{null}}")
    private String[] additionalMods;

    @Autowired
    public ServerInstanceServiceImpl(
            ServerRepository serverRepository,
            ServerInstanceInfoRepository instanceInfoRepository,
            FreeMarkerConfigurer freeMarkerConfigurer,
            PathsFactory pathsFactory
    ) {
        this.serverRepository = serverRepository;
        this.instanceInfoRepository = instanceInfoRepository;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.pathsFactory = pathsFactory;

        // Turn off all servers on application shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> instanceInfoRepository.getAll().stream()
                .map(ServerInstanceInfo::getProcess)
                .filter(Objects::nonNull)
                .filter(Process::isAlive)
                .forEach(Process::destroy)));
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
        setQueryPortForArma3Server(server);
        setInstanceIdForDayZServer(server);
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

    // Arma 3 server doesn't support customizing Steam query port, it's always game port + 1
    private void setQueryPortForArma3Server(Server server) {
        if (server.getType() == ServerType.ARMA3) {
            server.setQueryPort(server.getPort() + 1);
        }
    }

    private void setInstanceIdForDayZServer(Server server) {
        Long id = server.getId();
        if (id == null) {
            id = serverRepository.save(server).getId();
        }

        if (server.getType() == ServerType.DAYZ || server.getType() == ServerType.DAYZ_EXP) {
            ((DayZServer) server).setInstanceId(id);
        }
    }

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
        List<String> parameters = getParameters(server);

        File logFile = new File(logDir + File.separatorChar + server.getType().name() + "_" + server.getId() + ".log");
        LogUtils.prepareLogFile(logFile);

        try {
            log.info("Starting server with options: {}", Joiner.on(" ").join(parameters));

            ProcessBuilder pb = new ProcessBuilder(parameters)
                    .directory(pathsFactory.getServerPath(server.getType()).toFile());

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

    private List<String> getParameters(Server server) {
        List<String> parameters = new ArrayList<>();
        ServerType type = server.getType();

        parameters.add(pathsFactory.getServerExecutableWithFallback(server.getType()).toString());
        parameters.add("-port=" + server.getPort());
        parameters.add("-config=" + pathsFactory.getConfigFile(
                server.getType(), getConfigFileName(server.getType(), server.getId())).toString());

        if (type == ServerType.ARMA3) {
            parameters.add("-nosplash");
            parameters.add("-skipIntro");
            parameters.add("-world=empty");
            addArma3ModsAndDlcsToParameters(parameters, (Arma3Server) server);
        } else if (type == ServerType.DAYZ || type == ServerType.DAYZ_EXP) {
            parameters.add("-limitFPS=60");
            parameters.add("-dologs");
            parameters.add("-adminlog");
            parameters.add("-freezeCheck");
            addDayZModsToParameters(parameters, (DayZServer) server);
        }

        return parameters;
    }

    private void addArma3ModsAndDlcsToParameters(List<String> parameters, Arma3Server server) {
        // add enabled mods
        server.getActiveMods().stream() // TODO check installation status
                .map(mod -> "-mod=" + mod.getNormalizedName())
                .forEach(parameters::add);

        // add additional mods from properties
        if (additionalMods != null) {
            Arrays.stream(additionalMods)
                    .map(mod -> "-mod=" + mod)
                    .forEach(parameters::add);
        }

        // add enabled Creator DLCs
        server.getActiveDLCs().stream()
                .map(dlc -> "-mod=" + dlc.getId())
                .forEach(parameters::add);
    }

    private void addDayZModsToParameters(List<String> parameters, DayZServer server) {
        server.getActiveMods().stream() // TODO check installation status
                .map(mod -> "-mod=" + mod.getNormalizedName())
                .forEach(parameters::add);
    }

    private void writeConfig(Server server) {
        String configFileName = getConfigFileName(server.getType(), server.getId());
        File configFile = pathsFactory.getConfigFile(server.getType(), configFileName).toFile();

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
                    .getTemplate(Constants.SERVER_CONFIG_TEMPLATES.get(server.getType()));
            configTemplate.process(server, writer);
        } catch (IOException | TemplateException e) {
            log.error("Could not write config template due to {}", e.toString());
        }
    }

    private String getConfigFileName(ServerType type, Long serverId) {
        return type.name() + "_" + serverId + ".cfg";
    }
}
