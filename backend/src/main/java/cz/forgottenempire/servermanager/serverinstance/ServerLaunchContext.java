package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.Arma3KeyService;
import cz.forgottenempire.servermanager.common.PathsFactory;
import jakarta.annotation.Nullable;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

public record ServerLaunchContext(
        PathsFactory pathsFactory,
        Arma3InstancePaths arma3InstancePaths,
        Arma3KeyService arma3KeyService,
        FreeMarkerConfigurer freeMarkerConfigurer,
        @Nullable String[] additionalMods
) {
    public ServerLaunchContext(PathsFactory pathsFactory, Arma3InstancePaths arma3InstancePaths,
                               Arma3KeyService arma3KeyService, FreeMarkerConfigurer freeMarkerConfigurer) {
        this(pathsFactory, arma3InstancePaths, arma3KeyService, freeMarkerConfigurer, null);
    }
}
