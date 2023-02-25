package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsFacade;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
class ArmaLauncherPresetImportService {

    private static final String MODS_HREF_CSS_QUERY = ".mod-list a[href]";
    private static final String LINK_DELETE_REGEX = ".*\\?id=";

    private final WorkshopModsFacade modsFacade;
    private final ModPresetsService modPresetsService;

    @Autowired
    ArmaLauncherPresetImportService(WorkshopModsFacade modsFacade, ModPresetsService modPresetsService) {
        this.modsFacade = modsFacade;
        this.modPresetsService = modPresetsService;
    }

    Optional<ModPreset> importPreset(Document htmlPresetDocument) {
        Elements links = htmlPresetDocument.select(MODS_HREF_CSS_QUERY);
        List<Long> modIds = links.stream()
                .map(link -> link.attr("href").replaceFirst(LINK_DELETE_REGEX, ""))
                .map(this::convertModIdToLong)
                .toList();

        if (modIds.isEmpty()) {
            return Optional.empty();
        }

        List<WorkshopMod> importedMods = modsFacade.saveAndInstallMods(modIds);

        String modPresetName = determinePresetName(htmlPresetDocument);
        ModPreset modPreset = new ModPreset(modPresetName, importedMods, ServerType.ARMA3);
        modPreset = modPresetsService.savePreset(modPreset);

        return Optional.of(modPreset);
    }

    private Long convertModIdToLong(String modId) {
        try {
            return Long.parseLong(modId);
        } catch (NumberFormatException e) {
            throw new MalformedLauncherModPresetFileException(
                    "Invalid workshop mod ID '" + modId + "' found in preset HTML file"
            );
        }
    }

    private String determinePresetName(Document htmlPresetDocument) {
        Element element = htmlPresetDocument.selectFirst("meta[name=arma:PresetName]");
        String modPresetName;
        if (element != null && !modPresetsService.presetWithNameExists(element.attr("content"))) {
            modPresetName = element.attr("content");
        } else {
            modPresetName = generateImportedPresetName();
        }
        return modPresetName;
    }

    private String generateImportedPresetName() {
        int modPresetNumber = 1;
        String modPresetName = "Imported preset " + modPresetNumber;
        while (modPresetsService.presetWithNameExists(modPresetName)) {
            modPresetNumber++;
            modPresetName = "Imported preset " + modPresetNumber;
        }
        return modPresetName;
    }
}
