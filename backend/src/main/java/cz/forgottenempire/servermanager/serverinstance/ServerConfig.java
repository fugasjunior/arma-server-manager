package cz.forgottenempire.servermanager.serverinstance;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
public class ServerConfig {

    private final File configFile;
    private final String templateName;
    private final Object templateModel;
    private final FreeMarkerConfigurer freeMarkerConfigurer;
    @Nullable
    private final String rawOverride;

    public ServerConfig(File configFile, String templateName, Object model, FreeMarkerConfigurer freeMarkerConfigurer) {
        this(configFile, templateName, model, freeMarkerConfigurer, null);
    }

    public ServerConfig(File configFile, String templateName, Object model, FreeMarkerConfigurer freeMarkerConfigurer,
                        @Nullable String rawOverride) {
        this.configFile = configFile;
        this.templateName = templateName;
        this.templateModel = model;
        this.freeMarkerConfigurer = freeMarkerConfigurer;
        this.rawOverride = rawOverride;
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
        if (rawOverride != null) {
            writeRawOverride();
        } else {
            renderTemplateToFile();
        }
    }

    private void writeRawOverride() {
        ensureParentDirExists();
        log.info("Writing raw override config '{}'", configFile.getName());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            writer.write(Objects.requireNonNull(rawOverride));
        } catch (IOException e) {
            log.error("Could not write raw override config file", e);
        }
    }

    private void renderTemplateToFile() {
        ensureParentDirExists();
        log.info("Writing new server config '{}'", configFile.getName());
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(configFile))) {
            renderTemplate(writer);
        } catch (IOException | TemplateException e) {
            log.error("Could not write config file", e);
        }
    }

    private void ensureParentDirExists() {
        try {
            FileUtils.forceMkdirParent(configFile);
        } catch (IOException e) {
            log.error("Could not create directory structure for config file '{}'", configFile);
            throw new RuntimeException(e);
        }
    }

    private void renderTemplate(BufferedWriter writer) throws IOException, TemplateException {
        Template configTemplate = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
        configTemplate.setNumberFormat("computer");
        configTemplate.process(templateModel, writer);
    }

    public static String renderToString(FreeMarkerConfigurer freeMarkerConfigurer, String templateName, Object model) {
        try {
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(templateName);
            template.setNumberFormat("computer");
            StringWriter writer = new StringWriter();
            template.process(model, writer);
            return writer.toString();
        } catch (IOException | TemplateException e) {
            log.error("Could not render template '{}' to string", templateName, e);
            throw new RuntimeException("Failed to render template: " + templateName, e);
        }
    }

    public static String readFromFile(Path path) throws IOException {
        return Files.readString(path);
    }
}
