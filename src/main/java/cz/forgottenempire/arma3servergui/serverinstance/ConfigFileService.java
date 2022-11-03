package cz.forgottenempire.arma3servergui.serverinstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import cz.forgottenempire.arma3servergui.common.Constants;
import cz.forgottenempire.arma3servergui.common.PathsFactory;
import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.serverinstance.entities.Server;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

@Service
@Slf4j
class ConfigFileService {

    private final FreeMarkerConfigurer freeMarkerConfigurer;
    private final PathsFactory pathsFactory;

    @Autowired
    public ConfigFileService(FreeMarkerConfigurer freeMarkerConfigurer, PathsFactory pathsFactory) {
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.pathsFactory = pathsFactory;
    }

    private static void deleteOldConfigFile(File configFile) {
        if (!configFile.exists()) {
            return;
        }

        try {
            log.info("Deleting old configuration '{}'", configFile.getName());
            FileUtils.forceDelete(configFile);
        } catch (IOException e) {
            log.error("Could not delete old server config '{}' due to {}", configFile, e.toString());
        }
    }

    public File getConfigFileForServer(@NotNull Server server) {
        String extension = server.getType() == ServerType.REFORGER ? ".json" : ".cfg";
        String fileName = server.getType() + "_" + server.getId() + extension;
        return pathsFactory.getConfigFilePath(server.getType(), fileName).toFile();
    }

    public void writeConfig(@NotNull Server server) {
        File configFile = getConfigFileForServer(server);

        deleteOldConfigFile(configFile);

        // write new config file
        log.info("Writing new server config '{}'", configFile.getName());
        Template configTemplate;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            configTemplate = freeMarkerConfigurer.getConfiguration()
                    .getTemplate(Constants.SERVER_CONFIG_TEMPLATES.get(server.getType()));
            configTemplate.process(server, writer);
        } catch (IOException | TemplateException e) {
            log.error("Could not write config template", e);
        }
    }

    public Optional<String> readOptionFromConfig(@NotNull String key, @NotNull Server server) {
        if (server.getType() != ServerType.REFORGER) {
            throw new UnsupportedOperationException("Reading properties is only implemented for Reforger servers");
        }

        File configFile = getConfigFileForServer(server);
        if (!configFile.exists()) {
            throw new IllegalStateException("Cannot read property from config file " + configFile.getName()
                    + " because it does not exist");
        }

        try {
            Map<String, Object> values = new ObjectMapper().readValue(configFile, HashMap.class);
            String value = (String) values.get(key);
            return Optional.ofNullable(Strings.emptyToNull(value));
        } catch (Exception e) {
            log.error("Couldn't load property from config file {}", configFile.getName(), e);
        }
        return Optional.empty();
    }
}
