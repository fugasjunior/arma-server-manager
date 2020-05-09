package cz.forgottenempire.arma3servergui.services.impl;

import com.ibasco.agql.protocols.valve.source.query.client.SourceQueryClient;
import com.ibasco.agql.protocols.valve.source.query.pojos.SourceServer;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.model.ServerStatus;
import cz.forgottenempire.arma3servergui.model.WorkshopMod;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
        serverProcess = startServerProcess(settings);

        return serverProcess != null;
    }

    @Override
    public boolean shutDownServer() {
        if (serverProcess != null) {
            serverProcess.destroy();
            serverProcess = null;
        }
        return true;
    }

    @Override
    public ServerStatus getServerStatus() {
        SourceServer serverInfo = null;
        try (SourceQueryClient sourceQueryClient = new SourceQueryClient()) {
            InetSocketAddress serverAddress = new InetSocketAddress(hostName, 2303);
            serverInfo = sourceQueryClient.getServerInfo(serverAddress).get();
        } catch (Exception ignored) {}

        return ServerStatus.from(serverInfo);
    }

    private Process startServerProcess(ServerSettings settings) {
        Process serverProcess = null;

        List<String> parameters = new ArrayList<>();
        parameters.add("-nosplash");
        parameters.add("-skipIntro");
        parameters.add("-world=empty");
        parameters.add("-port=" + settings.getPort());
        parameters.add(getModsList(settings.getMods()));

        try {
            serverProcess = new ProcessBuilder()
                    .command(serverDir + File.separatorChar + "arma3server.exe", getModsList(settings.getMods()))
                    .inheritIO()
                    .start();
        } catch (IOException e) {
            logger.error("Could not start server due to {}", e.toString());
        }
        return serverProcess;
    }

    private void writeConfig(ServerSettings settings) {

    }

    private String getModsList(Collection<WorkshopMod> mods) {
        if (mods == null || mods.isEmpty()) return "";

        StringBuilder ret = new StringBuilder("-mod=");
        mods.forEach(mod -> ret.append(mod.getNormalizedName()).append(";"));
        ret.deleteCharAt(ret.lastIndexOf(";"));
        return ret.toString();
    }
}
