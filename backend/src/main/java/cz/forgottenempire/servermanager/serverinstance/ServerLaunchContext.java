package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.PathsFactory;
import jakarta.annotation.Nullable;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Map;

public record ServerLaunchContext(
        PathsFactory pathsFactory,
        Arma3InstancePaths arma3InstancePaths,
        FreeMarkerConfigurer freeMarkerConfigurer,
        @Nullable String[] additionalMods,
        @Nullable Map<ConfigFileKey, String> configOverrides
) {
    public ServerLaunchContext(PathsFactory pathsFactory, Arma3InstancePaths arma3InstancePaths,
                               FreeMarkerConfigurer freeMarkerConfigurer) {
        this(pathsFactory, arma3InstancePaths, freeMarkerConfigurer, null, null);
    }

    public ServerLaunchContext(PathsFactory pathsFactory, Arma3InstancePaths arma3InstancePaths,
                               FreeMarkerConfigurer freeMarkerConfigurer, @Nullable String[] additionalMods) {
        this(pathsFactory, arma3InstancePaths, freeMarkerConfigurer, additionalMods, null);
    }

    @Nullable
    public String getRawOverride(ConfigFileKey key) {
        return configOverrides != null ? configOverrides.get(key) : null;
    }
}
