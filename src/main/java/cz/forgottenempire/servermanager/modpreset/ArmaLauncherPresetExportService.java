package cz.forgottenempire.servermanager.modpreset;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

@Service
public class ArmaLauncherPresetExportService {

    public static final String TEMPLATE_FILE_NAME = "exportedModPresetHtml.ftl";
    private final FreeMarkerConfigurer freeMarkerConfigurer;

    @Autowired
    public ArmaLauncherPresetExportService(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.freeMarkerConfigurer = freeMarkerConfigurer;
    }

    public byte[] exportModPresetToFile(ModPreset modPreset) {
        try {
            Template template = freeMarkerConfigurer.getConfiguration().getTemplate(TEMPLATE_FILE_NAME);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Writer writer = new OutputStreamWriter(outputStream);
            template.process(modPreset, writer);
            writer.flush();
            return outputStream.toByteArray();
        } catch (TemplateException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
