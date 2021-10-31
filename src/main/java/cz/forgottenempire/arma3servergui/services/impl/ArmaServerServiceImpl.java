package cz.forgottenempire.arma3servergui.services.impl;

import com.google.common.base.Joiner;
import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.CreatorDLC;
import cz.forgottenempire.arma3servergui.model.DownloadStatus;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.repositories.CreatorDLCRepository;
import cz.forgottenempire.arma3servergui.repositories.WorkshopModRepository;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.util.LogUtils;
import cz.forgottenempire.arma3servergui.util.SteamCmdWrapper;
import cz.forgottenempire.steamcmd.SteamCmdParameterBuilder;
import cz.forgottenempire.steamcmd.SteamCmdParameters;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

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

    private WorkshopModRepository modRepository;
    private CreatorDLCRepository creatorDLCRepository;
    private SteamCmdWrapper steamCmdWrapper;

    private FreeMarkerConfigurer freeMarkerConfigurer;

    private Process serverProcess;

    private boolean serverUpdating;

    public ArmaServerServiceImpl() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (serverProcess != null) {
                serverProcess.destroy();
            }
        }));
    }

    @Override
    public boolean startServer(ServerSettings settings) {
        if (serverUpdating) {
            return false;
        }

        writeConfig(settings);
        serverProcess = startServerProcess(settings);

        return serverProcess != null;
    }

    @Override
    public void shutDownServer() {
        log.info("Shutting down server process");

        if (serverProcess != null) {
            serverProcess.destroy();
            serverProcess = null;
        }
    }

    @Override
    public boolean restartServer(ServerSettings settings) {
        shutDownServer();
        return startServer(settings);
    }

    @Override
    public boolean isServerProcessAlive() {
        return serverProcess != null && serverProcess.isAlive();
    }

    @Override
    public synchronized void updateServer(SteamAuth auth) {
        log.info("Updating server...");
        serverUpdating = true;
        shutDownServer();

        new Thread(() -> {
            SteamCmdParameters parameters = new SteamCmdParameterBuilder()
                    .withSteamGuardToken(auth.getSteamGuardToken())
                    .withLogin(auth.getUsername(), auth.getPassword())
                    .withInstallDir(serverDir)
                    .withAppInstall(Constants.STEAM_ARMA3SERVER_ID, true,
                            betaBranch == null ? "" : " -beta " + betaBranch)
                    .build();
            DownloadStatus status = steamCmdWrapper.execute(parameters);

            if (!status.isSuccess()) {
                log.error("Server update failed due to {}", status.getErrorStatus());
            }
            log.info("Server update done");
            serverUpdating = false;
        }).start();
    }

    @Override
    public boolean isServerUpdating() {
        return serverUpdating;
    }

    private Process startServerProcess(ServerSettings settings) {
        Process serverProcess = null;

        List<String> parameters = new ArrayList<>();
        parameters.add(getServerExecutable());
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.add("-config=" + getConfigFile().getAbsolutePath());
        parameters.add("-port=" + settings.getPort());

        List<String> mods = getActiveModsListAsParameters();
        if (!mods.isEmpty()) {
            parameters.addAll(mods);
        }

        // add additional mods from properties
        if (additionalMods != null && additionalMods.length > 0) {
            Arrays.stream(additionalMods)
                    .map(mod -> "-mod=" + mod)
                    .forEach(parameters::add);
        }

        // add enabled Creator DLCs
        for (CreatorDLC dlc : creatorDLCRepository.findAllByEnabledTrue()) {
            parameters.add("-mod=" + dlc.getGameId());
        }

        File logFile = new File(logDir + File.separatorChar + "out.log");
        LogUtils.prepareLogFile(logFile);

        try {
            log.info("Starting server with options: {}", Joiner.on(" ").join(parameters));

            ProcessBuilder pb = new ProcessBuilder(parameters)
                    .directory(new File(serverDir));

            if (logFile.exists()) {
                pb.redirectErrorStream(true).redirectOutput(ProcessBuilder.Redirect.appendTo(logFile));
            }
            serverProcess = pb.start();

            log.info("Server process started (pid {})", serverProcess.pid());
        } catch (IOException e) {
            log.error("Could not start server due to {}", e.toString());
        }
        return serverProcess;
    }

    private String getServerExecutable() {
        return Path.of(serverDir, x64Enabled ? "arma3server_x64" : "arma3server").toString();
    }

    private void writeConfig(ServerSettings settings) {
        // delete old config file
        try {
            log.info("Deleting old server.conf");
            FileUtils.forceDelete(getConfigFile());
        } catch (FileNotFoundException ignored) {
        } catch (IOException e) {
            log.error("Could not delete old server config due to {}", e.toString());
        }

        // write new config file
        log.info("Writing new server.conf");
        Template configTemplate;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getConfigFile()))) {
            configTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(Constants.TEMPLATE_SERVER_CONFIG);
            configTemplate.process(settings, writer);
        } catch (IOException | TemplateException e) {
            log.error("Could not write config template due to {}", e.toString());
        }
    }

    private List<String> getActiveModsListAsParameters() {
        return modRepository.findByActiveTrue().stream()
                .map(mod -> "-mod=" + mod.getNormalizedName())
                .collect(Collectors.toList());
    }

    private File getConfigFile() {
        return new File(serverDir + File.separatorChar + "server.cfg");
    }

    @Autowired
    public void setModRepository(WorkshopModRepository modRepository) {
        this.modRepository = modRepository;
    }

    @Autowired
    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }

    @Autowired
    public void setSteamCmdWrapper(SteamCmdWrapper steamCmdWrapper) {
        this.steamCmdWrapper = steamCmdWrapper;
    }

    @Autowired
    public void setCreatorDLCRepository(CreatorDLCRepository creatorDLCRepository) {
        this.creatorDLCRepository = creatorDLCRepository;
    }
}
