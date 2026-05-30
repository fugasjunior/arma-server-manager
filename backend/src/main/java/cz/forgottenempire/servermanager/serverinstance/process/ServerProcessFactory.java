package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.serverinstance.LogRotationProperties;
import cz.forgottenempire.servermanager.serverinstance.ServerLaunchContext;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Component
public class ServerProcessFactory {

    private final ServerProcessCreator serverProcessCreator;
    private final ServerLaunchContext launchContext;
    private final String[] additionalMods;
    private final int logMaxFiles;

    @Autowired
    public ServerProcessFactory(ServerProcessCreator serverProcessCreator,
                                PathsFactory pathsFactory,
                                Arma3InstancePaths arma3InstancePaths,
                                FreeMarkerConfigurer freeMarkerConfigurer,
                                @Value("${additionalMods:#{null}}") String[] additionalMods,
                                LogRotationProperties logRotationProperties) {
        this.serverProcessCreator = serverProcessCreator;
        this.launchContext = new ServerLaunchContext(pathsFactory, arma3InstancePaths, freeMarkerConfigurer, additionalMods);
        this.additionalMods = additionalMods;
        this.logMaxFiles = logRotationProperties.getMaxFiles();
    }

    public ServerProcess create(Server server) {
        if (server instanceof Arma3Server) {
            return new Arma3ServerProcess(server.getId(), serverProcessCreator, launchContext, additionalMods, logMaxFiles);
        }
        return new ServerProcess(server.getId(), serverProcessCreator, launchContext, logMaxFiles);
    }
}
