package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import jakarta.annotation.Nullable;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.Map;

public record ServerLaunchContext(
        PathsFactory pathsFactory,
        FreeMarkerConfigurer freeMarkerConfigurer,
        @Nullable String[] additionalMods,
        @Nullable Map<ConfigFileKey, String> configOverrides
) {
    public ServerLaunchContext(PathsFactory pathsFactory, FreeMarkerConfigurer freeMarkerConfigurer) {
        this(pathsFactory, freeMarkerConfigurer, null, null);
    }

    public ServerLaunchContext(PathsFactory pathsFactory, FreeMarkerConfigurer freeMarkerConfigurer,
                               @Nullable String[] additionalMods) {
        this(pathsFactory, freeMarkerConfigurer, additionalMods, null);
    }

    @Nullable
    public String getRawOverride(ConfigFileKey key) {
        return configOverrides != null ? configOverrides.get(key) : null;
    }
}
