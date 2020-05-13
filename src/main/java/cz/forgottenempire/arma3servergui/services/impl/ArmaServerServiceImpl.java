package cz.forgottenempire.arma3servergui.services.impl;

import com.google.common.base.Joiner;
import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.dtos.ServerStatus;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ArmaServerServiceImpl implements ArmaServerService {

    @Value("${serverDir}")
    private String serverDir;

    @Value("${hostName}")
    private String hostName;

    private JsonDbService<WorkshopMod> modDb;

    private FreeMarkerConfigurer freeMarkerConfigurer;

    private Process serverProcess;

    public ArmaServerServiceImpl() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (serverProcess != null) {
                serverProcess.destroy();
            }
        }));
    }

    @Override
    public boolean startServer(ServerSettings settings) {
        writeConfig(settings);
        serverProcess = startServerProcess(settings);

        return serverProcess != null;
    }

    @Override
    public boolean shutDownServer(ServerSettings settings) {
        log.info("Shutting down server process");

        if (serverProcess != null) {
            serverProcess.destroy();
            serverProcess = null;
        }
        return true;
    }

    @Override
    public ServerStatus getServerStatus(ServerSettings settings) {
        if (serverProcess == null || !serverProcess.isAlive()) {
            ServerStatus serverStatus = new ServerStatus();
            serverStatus.setServerUp(false);
            return serverStatus;
        }

        SourceServer serverInfo = null;
        try (SourceQueryClient sourceQueryClient = new SourceQueryClient()) {
            // default steam query port is (game server port + 1)
            InetSocketAddress serverAddress = new InetSocketAddress(hostName, settings.getPort() + 1);
            serverInfo = sourceQueryClient.getServerInfo(serverAddress).get();
        } catch (Exception ignored) {}

        return ServerStatus.from(serverInfo);
    }

    private Process startServerProcess(ServerSettings settings) {
        Process serverProcess = null;

        List<String> parameters = new ArrayList<>();
        parameters.add(serverDir + File.separatorChar + "arma3server.exe"); // TODO fix for linux
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.add("-config=" + getConfigFile().getAbsolutePath());
        parameters.add("-port=" + settings.getPort());

        List<String> mods = getModsList();
        if (!mods.isEmpty()) parameters.addAll(mods);

        try {
            log.info("Starting server with options: {}", Joiner.on(" ").join(parameters));

            serverProcess = new ProcessBuilder()
                    .command(parameters)
                    .inheritIO()
                    .start();

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
}
