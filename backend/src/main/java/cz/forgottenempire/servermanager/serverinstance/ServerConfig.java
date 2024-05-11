package cz.forgottenempire.servermanager.serverinstance;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
@Configurable
public class ServerConfig {

    private final File configFile;
    private final String templateName;
    private final Object templateModel;
    private FreeMarkerConfigurer freeMarkerConfigurer;

    public ServerConfig(File configFile, String templateName, Object model) {
        this.configFile = configFile;
        this.templateName = templateName;
        this.templateModel = model;
    }

    public void generateIfNecessary() {
        if (!configFile.exists()) {
            generate();
        }
    }

    public void generate() {
        deleteOldConfigFile();
        writeNewConfig();
    }

    private void deleteOldConfigFile() {
        if (!configFile.exists()) {
            return;
        }

        try {
            log.debug("Deleting old configuration '{}'", configFile.getName());
            FileUtils.forceDelete(configFile);
        } catch (IOException e) {
            log.error("Could not delete old server config '{}'", configFile, e);
        }
    }

    private void writeNewConfig() {
        try {
            FileUtils.forceMkdirParent(configFile);
        } catch (IOException e) {
            log.error("Could not create directory structure for config file '{}'", configFile);
            throw new RuntimeException(e);
        }

        log.info("Writing new server config '{}'", configFile.getName());
        Template configTemplate;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            configTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
            configTemplate.setNumberFormat("computer");
            configTemplate.process(templateModel, writer);
        } catch (IOException | TemplateException e) {
            log.error("Could not write config file", e);
        }
    }

    @Autowired
    void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }
}
