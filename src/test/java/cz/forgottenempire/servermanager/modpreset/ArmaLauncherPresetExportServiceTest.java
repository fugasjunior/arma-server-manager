package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ArmaLauncherPresetExportServiceTest {

    private static final String TEMPLATE_FILE_NAME = "exportedModPresetHtml.ftl";
    private static final String PRESET_NAME = "Mod Preset";

    @Test
    void whenExportModPresetToFile_thenCorrectByteArrayReturned() throws IOException, TemplateException {
        FreeMarkerConfigurer freeMarkerConfigurer = mock(FreeMarkerConfigurer.class);
        Configuration configuration = mock(Configuration.class);
        Template template = mock(Template.class);
        when(freeMarkerConfigurer.getConfiguration()).thenReturn(configuration);
        when(configuration.getTemplate(TEMPLATE_FILE_NAME)).thenReturn(template);
        ArmaLauncherPresetExportService presetExportService = new ArmaLauncherPresetExportService(freeMarkerConfigurer);
        ModPreset preset = new ModPreset(PRESET_NAME, Collections.emptyList(), ServerType.ARMA3);

        byte[] exportedPreset = presetExportService.exportModPresetToFile(preset);

        Map<String, Object> expectedModel = new HashMap<>();
        expectedModel.put("preset", preset);
        expectedModel.put("presetName", PRESET_NAME);
        assertThat(exportedPreset).isNotNull();
        InOrder inOrder = inOrder(freeMarkerConfigurer, configuration, template);
        inOrder.verify(freeMarkerConfigurer).getConfiguration();
        inOrder.verify(configuration).getTemplate(TEMPLATE_FILE_NAME);
        inOrder.verify(template).process(eq(expectedModel), any());
        verifyNoMoreInteractions(freeMarkerConfigurer, configuration, template);
    }
}
