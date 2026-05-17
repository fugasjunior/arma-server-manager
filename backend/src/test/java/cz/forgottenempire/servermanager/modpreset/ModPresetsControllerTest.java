package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.api.model.PresetResponseDto;
import cz.forgottenempire.servermanager.api.model.RenamePresetRequestDto;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NonUniqueNameException;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.workshop.WorkshopModsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class ModPresetsControllerTest {

    private static final long PRESET_ID = 1L;
    private static final String OLD_NAME = "Old Name";
    private static final String NEW_NAME = "New Name";

    private ModPresetsService modPresetsService;
    private ModPresetsController controller;

    @BeforeEach
    void setUp() {
        modPresetsService = mock(ModPresetsService.class);
        WorkshopModsService modsService = mock(WorkshopModsService.class);
        controller = new ModPresetsController(modPresetsService, modsService, Mappers.getMapper(ModPresetMapper.class));
    }

    @Test
    void whenRenamePreset_thenReturnUpdatedDto() {
        ModPreset preset = new ModPreset(OLD_NAME, Collections.emptyList(), ServerType.ARMA3);
        preset.setId(PRESET_ID);
        ModPreset renamed = new ModPreset(NEW_NAME, Collections.emptyList(), ServerType.ARMA3);
        renamed.setId(PRESET_ID);

        when(modPresetsService.getModPreset(PRESET_ID)).thenReturn(Optional.of(preset));
        when(modPresetsService.presetWithNameExists(NEW_NAME)).thenReturn(false);
        when(modPresetsService.updatePreset(any())).thenReturn(renamed);

        ResponseEntity<PresetResponseDto> response = controller.renamePreset(PRESET_ID,
                new RenamePresetRequestDto().name(NEW_NAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(NEW_NAME);
    }

    @Test
    void whenRenamePresetToSameName_thenReturnUpdatedDtoWithoutUniquenessCheck() {
        ModPreset preset = new ModPreset(OLD_NAME, Collections.emptyList(), ServerType.ARMA3);
        preset.setId(PRESET_ID);

        when(modPresetsService.getModPreset(PRESET_ID)).thenReturn(Optional.of(preset));
        when(modPresetsService.updatePreset(any())).thenReturn(preset);

        ResponseEntity<PresetResponseDto> response = controller.renamePreset(PRESET_ID,
                new RenamePresetRequestDto().name(OLD_NAME));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(modPresetsService, never()).presetWithNameExists(any());
    }

    @Test
    void whenRenamePresetToExistingName_thenThrowNonUniqueNameException() {
        ModPreset preset = new ModPreset(OLD_NAME, Collections.emptyList(), ServerType.ARMA3);
        preset.setId(PRESET_ID);

        when(modPresetsService.getModPreset(PRESET_ID)).thenReturn(Optional.of(preset));
        when(modPresetsService.presetWithNameExists(NEW_NAME)).thenReturn(true);

        assertThatThrownBy(() -> controller.renamePreset(PRESET_ID, new RenamePresetRequestDto().name(NEW_NAME)))
                .isInstanceOf(NonUniqueNameException.class);
    }

    @Test
    void whenRenameNonExistentPreset_thenThrowNotFoundException() {
        when(modPresetsService.getModPreset(PRESET_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> controller.renamePreset(PRESET_ID, new RenamePresetRequestDto().name(NEW_NAME)))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Mod preset " + PRESET_ID + " not found");
    }
}
