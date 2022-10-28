package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.common.exceptions.NotFoundException;
import cz.forgottenempire.arma3servergui.server.ServerType;
import java.util.Collection;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/mod/preset")
class ModPresetsController {

    private final ModPresetsService modPresetsService;
    private final ModPresetMapper modPresetMapper = Mappers.getMapper(ModPresetMapper.class);

    @Autowired
    public ModPresetsController(ModPresetsService modPresetsService) {
        this.modPresetsService = modPresetsService;
    }

    @GetMapping
    public ResponseEntity<ModPresetsDto> getAllPresets(@RequestParam(required = false) ServerType filter) {
        Collection<ModPreset> presets;
        if (filter == null) {
            presets = modPresetsService.getAllPresets();
        } else {
            presets = modPresetsService.getAllPresetsForServer(filter);
        }

        return ResponseEntity.ok(new ModPresetsDto(modPresetMapper.mapToModPresetDtos(presets)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModPresetDto> getPreset(@PathVariable Long id) {
        ModPreset modPreset = modPresetsService.getModPreset(id)
                .orElseThrow(() -> new NotFoundException("Mod preset " + id + " not found"));
        return ResponseEntity.ok(modPresetMapper.mapToModPresetDto(modPreset));
    }

    @PostMapping
    public ResponseEntity<ModPresetDto> createPreset(@Valid @RequestBody ModPresetDto modPresetDto) {
        // TODO
        ModPreset modPreset = modPresetsService.savePreset(modPresetMapper.mapModPresetDtoToEntity(modPresetDto));
        return ResponseEntity.ok(modPresetMapper.mapToModPresetDto(modPreset));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModPresetDto> updatePreset(
            @PathVariable Long id,
            @Valid @RequestBody ModPresetDto modPresetDto
    ) {
        ModPreset modPreset = modPresetsService.getModPreset(id)
                .orElseThrow(() -> new NotFoundException("Mod preset " + id + " not found"));
        modPresetMapper.updateModPresetFromDto(modPreset, modPresetDto);
        modPreset = modPresetsService.savePreset(modPreset);
        return ResponseEntity.ok(modPresetMapper.mapToModPresetDto(modPreset));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePreset(@PathVariable Long id) {
        modPresetsService.deletePreset(id);
        return ResponseEntity.noContent().build();
    }
}
