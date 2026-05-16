package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.PathsFactory;
import jakarta.persistence.PostLoad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Component
public class ServerEntityListener {

    private final PathsFactory pathsFactory;
    private final FreeMarkerConfigurer freeMarkerConfigurer;
    private final String[] additionalMods;

    @Autowired
    public ServerEntityListener(PathsFactory pathsFactory, FreeMarkerConfigurer freeMarkerConfigurer,
                                @Value("${additionalMods:#{null}}") String[] additionalMods) {
        this.pathsFactory = pathsFactory;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.additionalMods = additionalMods;
    }

    @PostLoad
    public void postLoad(Server server) {
        server.setPathsFactory(pathsFactory);
        server.setFreeMarkerConfigurer(freeMarkerConfigurer);
        if (server instanceof Arma3Server arma3Server) {
            arma3Server.setAdditionalMods(additionalMods);
        }
    }
}
