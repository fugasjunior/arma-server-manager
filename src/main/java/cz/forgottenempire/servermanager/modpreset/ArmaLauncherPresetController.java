package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    private String getPresetFileName(ModPreset preset) {
        // replaces all unsupported file name characters and creates a .html file from the mod name
        return preset.getName().replaceAll("[^a-zA-Z0-9-_.]", "_") + ".html";
    }
}
