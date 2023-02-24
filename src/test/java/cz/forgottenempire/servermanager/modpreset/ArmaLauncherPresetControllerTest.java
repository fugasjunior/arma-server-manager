package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ArmaLauncherPresetControllerTest {

    public static final long MOD_PRESET_ID = 1L;
    private ArmaLauncherPresetExportService exportService;
    private ModPresetsService modPresetsService;
    private ArmaLauncherPresetController controller;

    @BeforeEach
    void setUp() {
        ArmaLauncherPresetImportService importService = mock(ArmaLauncherPresetImportService.class, withSettings().stubOnly());
        exportService = mock(ArmaLauncherPresetExportService.class);
        modPresetsService = mock(ModPresetsService.class);
        controller = new ArmaLauncherPresetController(modPresetsService, importService, exportService);
    }

    @Test
    void whenExportModPreset_thenResponseEntityWithModPresetReturned() {
        ModPreset preset = new ModPreset("Test Preset", Collections.emptyList(), ServerType.ARMA3);
        when(modPresetsService.getModPreset(MOD_PRESET_ID)).thenReturn(Optional.of(preset));
        byte[] expectedFile = new byte[]{'h', 't', 'm', 'l'};
        when(exportService.exportModPresetToFile(preset)).thenReturn(expectedFile);

        ResponseEntity<byte[]> response = controller.exportModPreset(MOD_PRESET_ID);

        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(response).isNotNull();
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        softly.assertThat(response.getBody()).isEqualTo(expectedFile);
        HttpHeaders headers = response.getHeaders();
        softly.assertThat(headers).isNotNull();
        softly.assertThat(headers.getContentType()).isEqualTo(MediaType.APPLICATION_OCTET_STREAM);
        softly.assertThat(headers.getContentDisposition().getFilename()).isEqualTo("Test_Preset.html");
        softly.assertAll();
    }

    @Test
    void whenExportModPresetWithUnsupportedCharactersInName_thenCharactersAreReplacedByUnderscoresInFileName() {
        ModPreset preset = new ModPreset("a#b$c%d 1234 e-f_g", Collections.emptyList(), ServerType.ARMA3);
        when(modPresetsService.getModPreset(MOD_PRESET_ID)).thenReturn(Optional.of(preset));
        byte[] expectedFile = new byte[]{'h', 't', 'm', 'l'};
        when(exportService.exportModPresetToFile(preset)).thenReturn(expectedFile);

        ResponseEntity<byte[]> response = controller.exportModPreset(MOD_PRESET_ID);

        assertThat(response).isNotNull();
        HttpHeaders headers = response.getHeaders();
        assertThat(headers).isNotNull();
        assertThat(headers.getContentDisposition().getFilename()).isEqualTo("a_b_c_d_1234_e-f_g.html");
    }

    @Test
    void whenExportModPresetAndNoSuchPresetExists_thenThrowNotFoundException() {
        when(modPresetsService.getModPreset(MOD_PRESET_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.exportModPreset(MOD_PRESET_ID))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Mod preset 1 not found");
        verify(modPresetsService).getModPreset(MOD_PRESET_ID);
        verifyNoMoreInteractions(modPresetsService);
        verifyNoInteractions(exportService);
    }
}