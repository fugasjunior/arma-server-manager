package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.modpreset.dtos.PresetResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/api/mod/launcher_preset")
class ArmaLauncherPresetController {

    private final ModPresetsService modPresetsService;
    private final ArmaLauncherPresetImportService importService;
    private final ArmaLauncherPresetExportService exportService;
    private final ModPresetMapper modPresetMapper = Mappers.getMapper(ModPresetMapper.class);

    @Autowired
    ArmaLauncherPresetController(
            ModPresetsService modPresetsService,
            ArmaLauncherPresetImportService importService,
            ArmaLauncherPresetExportService exportService
    ) {
        this.modPresetsService = modPresetsService;
        this.importService = importService;
        this.exportService = exportService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> exportModPreset(@PathVariable long id) {
        ModPreset modPreset = modPresetsService.getModPreset(id)
                .orElseThrow(() -> new NotFoundException("Mod preset " + id + " not found"));

        byte[] modPresetHtml = exportService.exportModPresetToFile(modPreset);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(getPresetFileName(modPreset))
                        .build()
        );

        return new ResponseEntity<>(modPresetHtml, headers, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PresetResponseDto> uploadModPreset(@RequestParam("preset") MultipartFile presetFile) throws IOException {
        if (!presetFile.getName().toLowerCase().endsWith(".html")) {
            throw new UnsupportedFileExtension();
        }

        byte[] fileBytes = presetFile.getBytes();
        String fileContent = new String(fileBytes);
        Optional<ModPreset> modPreset = importService.importPreset(Jsoup.parse(fileContent));

        return modPreset.map(preset -> ResponseEntity.ok(modPresetMapper.mapToModPresetDto(preset)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private String getPresetFileName(ModPreset preset) {
        // replaces all unsupported file name characters and creates a .html file from the mod name
        return preset.getName().replaceAll("[^a-zA-Z0-9-_.]", "_") + ".html";
    }
}
