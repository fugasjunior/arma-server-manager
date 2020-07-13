package cz.forgottenempire.arma3servergui.services.impl;

import com.google.common.base.Joiner;
import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.ServerStatus;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import cz.forgottenempire.arma3servergui.util.LogUtils;
import cz.forgottenempire.arma3servergui.util.SteamCmdWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

    private JsonDbService<WorkshopMod> modDb;
    private SteamCmdWrapper steamCmdWrapper;
    private ServerStatus serverStatus;

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
            List<String> args = new ArrayList<>();
            args.add("+@NoPromptForPassword 1");
            args.add("+@ShutdownOnFailedCommand 1");

            String token = auth.getSteamGuardToken();
            if (token != null && !token.isBlank()) {
                args.add("+set_steam_guard_code " + token);
            }

            args.add("+login " + auth.getUsername() + " " + auth.getPassword());
            args.add("+force_install_dir");
            args.add(serverDir);
            args.add("+app_update " + Constants.STEAM_ARMA3SERVER_ID +
                    (betaBranch == null ? "" : " -beta " + betaBranch) +
                    " validate");
            args.add("+quit");

            try {
                steamCmdWrapper.execute(args);
            } catch (IOException | InterruptedException e) {
                log.error("Error during server update: {}", e.toString());
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
        parameters.add(serverDir + File.separatorChar + "arma3server");
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.add("-config=" + getConfigFile().getAbsolutePath());
        parameters.add("-port=" + settings.getPort());

        List<String> mods = getModsList();
        if (!mods.isEmpty()) parameters.addAll(mods);

        // add additional mods from properties
        if (additionalMods != null && additionalMods.length > 0) {
            Arrays.stream(additionalMods)
                    .map(mod -> "-mod=" + mod)
                    .forEach(parameters::add);
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

    private void writeConfig(ServerSettings settings) {
        // delete old config file
        try {
            log.info("Deleting old server.conf");
            FileUtils.forceDelete(getConfigFile());
        } catch (FileNotFoundException ignored) {} catch (IOException e) {
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

    private List<String> getModsList() {
        return modDb.findAll(WorkshopMod.class).stream()
                .filter(WorkshopMod::isActive)
                .map(mod -> "-mod=" + mod.getNormalizedName())
                .collect(Collectors.toList());
    }

    private File getConfigFile() {
        return new File(serverDir + File.separatorChar + "server.cfg");
    }

    @Autowired
    public void setModDb(JsonDbService<WorkshopMod> modDb) {
        this.modDb = modDb;
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
    public void setServerStatus(ServerStatus serverStatus) {
        this.serverStatus = serverStatus;
    }
}
