package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.api.ModPresetsApi;
import cz.forgottenempire.servermanager.api.model.CreatePresetRequestDto;
import cz.forgottenempire.servermanager.api.model.PresetListResponseDto;
import cz.forgottenempire.servermanager.api.model.PresetResponseDto;
import cz.forgottenempire.servermanager.api.model.RenamePresetRequestDto;
import cz.forgottenempire.servermanager.api.model.UpdatePresetRequestDto;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NonUniqueNameException;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsService;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
class ModPresetsController implements ModPresetsApi {

    private final ModPresetsService modPresetsService;
    private final WorkshopModsService modsService;
    private final ModPresetMapper modPresetMapper;

    @Autowired
    public ModPresetsController(
            ModPresetsService modPresetsService,
            WorkshopModsService modsService,
            ModPresetMapper modPresetMapper
    ) {
        this.modPresetsService = modPresetsService;
        this.modsService = modsService;
        this.modPresetMapper = modPresetMapper;
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_VIEW + "')")
    public ResponseEntity<PresetListResponseDto> getPresets(ServerType filter) {
        Collection<ModPreset> presets = filter == null
                ? modPresetsService.getAllPresets()
                : modPresetsService.getAllPresetsForServer(filter);
        return ResponseEntity.ok(new PresetListResponseDto().presets(modPresetMapper.mapToModPresetDtos(presets)));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_VIEW + "')")
    public ResponseEntity<PresetResponseDto> getPreset(Long id) {
        ModPreset modPreset = modPresetsService.getModPreset(id)
                .orElseThrow(() -> new NotFoundException("Mod preset " + id + " not found"));
        return ResponseEntity.ok(modPresetMapper.mapToModPresetDto(modPreset));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_MODIFY + "')")
    public ResponseEntity<PresetResponseDto> createPreset(CreatePresetRequestDto requestDto) {
        validatePresetName(requestDto.getName());
        List<WorkshopMod> mods = validateAndGetMods(requestDto.getMods(), requestDto.getType());
        ModPreset modPreset = new ModPreset(requestDto.getName(), mods, requestDto.getType());
        modPreset = modPresetsService.savePreset(modPreset);
        return ResponseEntity.ok(modPresetMapper.mapToModPresetDto(modPreset));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_MODIFY + "')")
    public ResponseEntity<PresetResponseDto> updatePreset(Long id, UpdatePresetRequestDto requestDto) {
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

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_MODIFY + "')")
    public ResponseEntity<PresetResponseDto> renamePreset(Long id, RenamePresetRequestDto requestDto) {
        ModPreset modPreset = modPresetsService.getModPreset(id)
                .orElseThrow(() -> new NotFoundException("Mod preset " + id + " not found"));
        if (!modPreset.getName().equals(requestDto.getName())) {
            validatePresetName(requestDto.getName());
        }
        modPreset.setName(requestDto.getName());
        modPreset = modPresetsService.updatePreset(modPreset);
        return ResponseEntity.ok(modPresetMapper.mapToModPresetDto(modPreset));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.MOD_DELETE + "')")
    public ResponseEntity<Void> deletePreset(Long id) {
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
