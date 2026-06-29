package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.Arma3KeyService;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.ConfigFileKey;
import cz.forgottenempire.servermanager.serverinstance.ConfigOverrideService;
import cz.forgottenempire.servermanager.serverinstance.LogRotationProperties;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Map;

@Component
public class ServerProcessFactory {

    private final ServerProcessCreator serverProcessCreator;
    private final PathsFactory pathsFactory;
    private final Arma3InstancePaths arma3InstancePaths;
    private final Arma3KeyService arma3KeyService;
    private final FreeMarkerConfigurer freeMarkerConfigurer;
    private final String[] additionalMods;
    private final int logMaxFiles;
    private final ConfigOverrideService configOverrideService;

    @Autowired
    public ServerProcessFactory(ServerProcessCreator serverProcessCreator,
                                PathsFactory pathsFactory,
                                Arma3InstancePaths arma3InstancePaths,
                                Arma3KeyService arma3KeyService,
                                FreeMarkerConfigurer freeMarkerConfigurer,
                                @Value("${additionalMods:#{null}}") String[] additionalMods,
                                LogRotationProperties logRotationProperties,
                                ConfigOverrideService configOverrideService) {
        this.serverProcessCreator = serverProcessCreator;
        this.pathsFactory = pathsFactory;
        this.arma3InstancePaths = arma3InstancePaths;
        this.arma3KeyService = arma3KeyService;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.additionalMods = additionalMods;
        this.logMaxFiles = logRotationProperties.getMaxFiles();
        this.configOverrideService = configOverrideService;
    }

    public ServerProcess create(Server server) {
        Map<ConfigFileKey, String> overrides = configOverrideService.loadOverridesMap(server.getId());
        ServerLaunchContext ctx = new ServerLaunchContext(
                pathsFactory, arma3InstancePaths, arma3KeyService, freeMarkerConfigurer, additionalMods,
                overrides.isEmpty() ? null : overrides);
        if (server instanceof Arma3Server) {
            return new Arma3ServerProcess(server.getId(), serverProcessCreator, ctx, additionalMods, logMaxFiles);
        }
        return new ServerProcess(server.getId(), serverProcessCreator, ctx, logMaxFiles);
    }
}
