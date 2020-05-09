package cz.forgottenempire.arma3servergui.services.impl;

import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.ServerStatus;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class ArmaServerServiceImpl implements ArmaServerService {

    private final Logger logger = LoggerFactory.getLogger(ArmaServerServiceImpl.class);

    @Value("${serverDir}")
    private String serverDir;

    @Value("${hostName}")
    private String hostName;

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
        if (serverProcess != null) {
            serverProcess.destroy();
            serverProcess = null;
        }
        return true;
    }

    @Override
    public ServerStatus getServerStatus(ServerSettings settings) {
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

        String mods = getModsList(settings.getMods());
        if (!mods.isEmpty()) parameters.add(mods);

        try {
            serverProcess = new ProcessBuilder()
                    .command(parameters)
                    .inheritIO()
                    .start();
        } catch (IOException e) {
            logger.error("Could not start server due to {}", e.toString());
        }
        return serverProcess;
    }

    private void writeConfig(ServerSettings settings) {
        // delete old config file
        try {
            FileUtils.forceDelete(getConfigFile());
        } catch (FileNotFoundException ignored) {} catch (IOException e) {
            logger.error("Could not delete old server config due to {}", e.toString());
        }

        // write new config file
        Template configTemplate;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getConfigFile()))) {
            configTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(Constants.TEMPLATE_SERVER_CONFIG);
            configTemplate.process(settings, writer);
        } catch (IOException | TemplateException e) {
            logger.error("Could not write config template due to {}", e.toString());
        }
    }

    private String getModsList(Collection<WorkshopMod> mods) {
        if (mods == null || mods.isEmpty()) return "";

        StringBuilder ret = new StringBuilder("-mod=");
        mods.forEach(mod -> ret.append(mod.getNormalizedName()).append(";"));
        ret.deleteCharAt(ret.lastIndexOf(";"));
        return ret.toString();
    }

    private File getConfigFile() {
        return new File(serverDir + File.separatorChar + "server.cfg");
    }

    @Autowired
    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }
}
