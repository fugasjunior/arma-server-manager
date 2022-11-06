package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NonUniqueNameException;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.modpreset.dtos.CreatePresetRequestDto;
import cz.forgottenempire.servermanager.modpreset.dtos.PresetListResponseDto;
import cz.forgottenempire.servermanager.modpreset.dtos.PresetResponseDto;
import cz.forgottenempire.servermanager.modpreset.dtos.UpdatePresetRequestDto;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsService;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
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
    private final WorkshopModsService modsService;
    private final ModPresetMapper modPresetMapper = Mappers.getMapper(ModPresetMapper.class);

    @Autowired
    public ModPresetsController(ModPresetsService modPresetsService, WorkshopModsService modsService) {
        this.modPresetsService = modPresetsService;
        this.modsService = modsService;
    }

    @GetMapping
    public ResponseEntity<PresetListResponseDto> getAllPresets(@RequestParam(required = false) ServerType filter) {
        Collection<ModPreset> presets;
        if (filter == null) {
            presets = modPresetsService.getAllPresets();
        } else {
            presets = modPresetsService.getAllPresetsForServer(filter);
        }

        return ResponseEntity.ok(new PresetListResponseDto(modPresetMapper.mapToModPresetDtos(presets)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PresetResponseDto> getPreset(@PathVariable Long id) {
        ModPreset modPreset = modPresetsService.getModPreset(id)
                .orElseThrow(() -> new NotFoundException("Mod preset " + id + " not found"));
        return ResponseEntity.ok(modPresetMapper.mapToModPresetDto(modPreset));
    }

    @PostMapping
    public ResponseEntity<PresetResponseDto> createPreset(@Valid @RequestBody CreatePresetRequestDto requestDto) {
        validatePresetName(requestDto.getName());
        List<WorkshopMod> mods = validateAndGetMods(requestDto.getMods(), requestDto.getType());
        ModPreset modPreset = new ModPreset(requestDto.getName(), mods, requestDto.getType());
        modPreset = modPresetsService.savePreset(modPreset);
        return ResponseEntity.ok(modPresetMapper.mapToModPresetDto(modPreset));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PresetResponseDto> updatePreset(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePresetRequestDto requestDto
    ) {
        ModPreset modPreset = modPresetsService.getModPreset(id)
                .orElseThrow(() -> new NotFoundException("Mod preset " + id + " not found"));
        List<WorkshopMod> mods = validateAndGetMods(requestDto.getMods(), modPreset.getType());

        if (!modPreset.getName().equals(requestDto.getName())) {
            validatePresetName(requestDto.getName());
        }

        modPreset.setName(requestDto.getName());
        modPreset.setMods(mods);
        modPreset = modPresetsService.updatePreset(modPreset);
        return ResponseEntity.ok(modPresetMapper.mapToModPresetDto(modPreset));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePreset(@PathVariable Long id) {
        modPresetsService.deletePreset(id);
        return ResponseEntity.noContent().build();
    }

    private List<WorkshopMod> validateAndGetMods(List<Long> mods, ServerType type) {
        return mods.stream()
                .map(modId -> modsService.getMod(modId)
                        .orElseThrow(() -> new NotFoundException("Mod id " + modId + " is not installed.")))
                .peek(mod -> {
                    if (mod.getServerType() != type) {
                        throw new ModTypeDiffersFromPresetException(mod.getId(), mod.getServerType(), type);
                    }
                })
                .collect(Collectors.toList());
    }

    private void validatePresetName(String name) {
        if (modPresetsService.presetWithNameExists(name)) {
            throw new NonUniqueNameException("Preset name '" + name + "' is already used");
        }
    }
}
