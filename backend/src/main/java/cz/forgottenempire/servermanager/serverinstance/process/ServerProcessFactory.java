package cz.forgottenempire.servermanager.serverinstance.process;

import cz.forgottenempire.servermanager.common.PathsFactory;
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

    @Autowired
    public ServerProcessFactory(ServerProcessCreator serverProcessCreator,
                                PathsFactory pathsFactory,
                                FreeMarkerConfigurer freeMarkerConfigurer,
                                @Value("${additionalMods:#{null}}") String[] additionalMods) {
        this.serverProcessCreator = serverProcessCreator;
        this.launchContext = new ServerLaunchContext(pathsFactory, freeMarkerConfigurer, additionalMods);
        this.additionalMods = additionalMods;
    }

    public ServerProcess create(Server server) {
        if (server instanceof Arma3Server) {
            return new Arma3ServerProcess(server.getId(), serverProcessCreator, launchContext, additionalMods);
        }
        return new ServerProcess(server.getId(), serverProcessCreator, launchContext);
    }
}
