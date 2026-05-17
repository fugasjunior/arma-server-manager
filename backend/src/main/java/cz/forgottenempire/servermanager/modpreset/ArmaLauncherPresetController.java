package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.api.ArmaLauncherPresetApi;
import cz.forgottenempire.servermanager.api.model.PresetResponseDto;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import java.io.IOException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
class ArmaLauncherPresetController implements ArmaLauncherPresetApi {

    private final ModPresetsService modPresetsService;
    private final ArmaLauncherPresetImportService importService;
    private final ArmaLauncherPresetExportService exportService;
    private final ModPresetMapper modPresetMapper;

    @Autowired
    ArmaLauncherPresetController(
            ModPresetsService modPresetsService,
            ArmaLauncherPresetImportService importService,
            ArmaLauncherPresetExportService exportService,
            ModPresetMapper modPresetMapper
    ) {
        this.modPresetsService = modPresetsService;
        this.importService = importService;
        this.exportService = exportService;
        this.modPresetMapper = modPresetMapper;
    }

    @Override
    public ResponseEntity<Resource> downloadLauncherPreset(Long id) {
        ModPreset modPreset = modPresetsService.getModPreset(id)
                .orElseThrow(() -> new NotFoundException("Mod preset " + id + " not found"));

        byte[] modPresetHtml = exportService.exportModPresetToFile(modPreset);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.setContentDisposition(
                ContentDisposition.builder("attachment")
                        .filename(getPresetFileName(modPreset))
                        .build()
        );

        return ResponseEntity.ok().headers(headers).body(new ByteArrayResource(modPresetHtml));
    }

    @Override
    public ResponseEntity<PresetResponseDto> importLauncherPreset(MultipartFile preset, String name) {
        if (preset == null) {
            return ResponseEntity.noContent().build();
        }

        String filename = preset.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".html")) {
            throw new UnsupportedFileExtension();
        }

        String fileContent;
        try {
            fileContent = new String(preset.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to read preset file", e);
        }
        Optional<ModPreset> modPreset = importService.importPreset(Jsoup.parse(fileContent), name);

        return modPreset.map(p -> ResponseEntity.ok(modPresetMapper.mapToModPresetDto(p)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    private String getPresetFileName(ModPreset preset) {
        return preset.getName().replaceAll("[^a-zA-Z0-9-_.]", "_") + ".html";
    }
}
