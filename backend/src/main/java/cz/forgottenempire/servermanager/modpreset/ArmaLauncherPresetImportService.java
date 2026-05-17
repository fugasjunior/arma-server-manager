package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NonUniqueNameException;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsFacade;
import org.jsoup.nodes.Document;
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

    Optional<ModPreset> importPreset(Document htmlPresetDocument, String presetName) {
        Elements links = htmlPresetDocument.select(MODS_HREF_CSS_QUERY);
        List<Long> modIds = links.stream()
                .map(link -> link.attr("href").replaceFirst(LINK_DELETE_REGEX, ""))
                .map(this::convertModIdToLong)
                .toList();

        if (modIds.isEmpty()) {
            return Optional.empty();
        }

        if (modPresetsService.presetWithNameExists(presetName)) {
            throw new NonUniqueNameException("Preset name '" + presetName + "' is already used");
        }

        List<WorkshopMod> importedMods = modsFacade.saveAndInstallMods(modIds);
        ModPreset modPreset = new ModPreset(presetName, importedMods, ServerType.ARMA3);
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
}
