package cz.forgottenempire.servermanager.serverinstance;

import cz.forgottenempire.servermanager.common.PathsFactory;
import jakarta.annotation.Nullable;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

public record ServerLaunchContext(
        PathsFactory pathsFactory,
        FreeMarkerConfigurer freeMarkerConfigurer,
        @Nullable String[] additionalMods
) {
    public ServerLaunchContext(PathsFactory pathsFactory, FreeMarkerConfigurer freeMarkerConfigurer) {
        this(pathsFactory, freeMarkerConfigurer, null);
    }
}
