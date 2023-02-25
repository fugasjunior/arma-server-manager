package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.modpreset.dtos.PresetResponseDto;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ArmaLauncherPresetControllerTest {

    private static final long MOD_PRESET_ID = 1L;
    private static final String MOD_PRESET_NAME = "Test Preset";
    private static final String INVALID_FILE_NAME = "invalidfile.bin";
    private static final String VALID_FILE_NAME = "preset.html";
    private ArmaLauncherPresetExportService exportService;
    private ModPresetsService modPresetsService;
    private ArmaLauncherPresetController controller;
    private String SAMPLE_HTML_CONTENT;
    private ArmaLauncherPresetImportService importService;

    @BeforeEach
    void setUp() {
        importService = mock(ArmaLauncherPresetImportService.class);
        exportService = mock(ArmaLauncherPresetExportService.class);
        modPresetsService = mock(ModPresetsService.class);
        controller = new ArmaLauncherPresetController(modPresetsService, importService, exportService);
        SAMPLE_HTML_CONTENT = "<html></html>";
    }

    @Test
    void whenExportModPreset_thenResponseEntityWithModPresetReturned() {
        ModPreset preset = new ModPreset(MOD_PRESET_NAME, Collections.emptyList(), ServerType.ARMA3);
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

    @Test
    void whenImportModPreset_thenCreatedModPresetIsReturned() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(VALID_FILE_NAME);
        when(file.getBytes()).thenReturn(SAMPLE_HTML_CONTENT.getBytes());
        ModPreset preset = new ModPreset(MOD_PRESET_NAME, Collections.emptyList(), ServerType.ARMA3);
        preset.setId(MOD_PRESET_ID);
        when(importService.importPreset(any())).thenReturn(Optional.of(preset));

        ResponseEntity<PresetResponseDto> response = controller.uploadModPreset(file);

        PresetResponseDto expectedResponseDto = new PresetResponseDto();
        expectedResponseDto.setId(MOD_PRESET_ID);
        expectedResponseDto.setName(MOD_PRESET_NAME);
        expectedResponseDto.setMods(Collections.emptyList());
        expectedResponseDto.setType(ServerType.ARMA3);
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedResponseDto);
    }

    @Test
    void whenImportModPresetWithNoMods_thenNoContentIsReturned() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(VALID_FILE_NAME);
        when(file.getBytes()).thenReturn(SAMPLE_HTML_CONTENT.getBytes());
        when(importService.importPreset(any())).thenReturn(Optional.empty());

        ResponseEntity<PresetResponseDto> response = controller.uploadModPreset(file);

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    void whenImportingInvalidFileExtension_thenUnsupportedFileExtensionThrown() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(INVALID_FILE_NAME);

        assertThatThrownBy(() -> controller.uploadModPreset(file))
                .isInstanceOf(UnsupportedFileExtension.class)
                .hasMessage("Only HTML files are allowed");
        verifyNoInteractions(importService);
    }

    @Test
    void whenImportingFileWithFileNameNull_thenUnsupportedFileExtensionThrown() {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(null);

        assertThatThrownBy(() -> controller.uploadModPreset(file))
                .isInstanceOf(UnsupportedFileExtension.class)
                .hasMessage("Only HTML files are allowed");
        verifyNoInteractions(importService);
    }
}