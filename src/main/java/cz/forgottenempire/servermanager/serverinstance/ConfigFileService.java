package cz.forgottenempire.servermanager.serverinstance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import cz.forgottenempire.servermanager.common.Constants;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3DifficultySettings;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
import cz.forgottenempire.servermanager.serverinstance.entities.Server;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.validation.constraints.NotNull;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

    public File getConfigFileForServer(@NotNull Server server) {
        String extension = server.getType() == ServerType.REFORGER ? ".json" : ".cfg";
        String fileName = server.getType() + "_" + server.getId() + extension;
        return pathsFactory.getConfigFilePath(server.getType(), fileName).toFile();
    }

    public void writeConfig(@NotNull Server server) {
        File configFile = getConfigFileForServer(server);
        deleteOldConfigFile(configFile);
        writeNewConfig(server, configFile);

        if (server.getType() == ServerType.ARMA3) {
            Arma3Server arma3Server = (Arma3Server) server;
            File profileFile = pathsFactory.getServerProfileFile(arma3Server.getId());
            deleteOldConfigFile(profileFile);
            writeNewProfile(arma3Server.getDifficultySettings(), profileFile);
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

    private static void deleteOldConfigFile(File configFile) {
        if (!configFile.exists()) {
            return;
        }

        try {
            log.info("Deleting old configuration '{}'", configFile.getName());
            FileUtils.forceDelete(configFile);
        } catch (IOException e) {
            log.error("Could not delete old server config '{}'", configFile, e);
        }
    }

    private void writeNewConfig(Server server, File configFile) {
        log.info("Writing new server config '{}'", configFile.getName());
        Template configTemplate;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            configTemplate = freeMarkerConfigurer.getConfiguration()
                    .getTemplate(Constants.SERVER_CONFIG_TEMPLATES.get(server.getType()));
            configTemplate.process(server, writer);
        } catch (IOException | TemplateException e) {
            log.error("Could not write config file", e);
        }
    }

    private void writeNewProfile(Arma3DifficultySettings difficultySettings, File profileFile) {
        try {
            FileUtils.forceMkdirParent(profileFile);
        } catch (IOException e) {
            log.error("Could not create directory structure for profile '{}'", profileFile.getAbsolutePath());
            throw new RuntimeException(e);
        }

        log.info("Writing new server profile '{}'", profileFile.getName());
        Template configTemplate;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(profileFile))) {
            configTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(Constants.ARMA3_PROFILE_TEMPLATE);
            configTemplate.process(difficultySettings, writer);
        } catch (IOException | TemplateException e) {
            log.error("Could not write profile file", e);
        }
    }
}
