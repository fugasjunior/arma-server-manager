package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsFacade;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArmaLauncherPresetService {

    public static final String MODS_HREF_CSS_QUERY = ".mod-list a[href]";
    public static final String LINK_DELETE_REGEX = ".*\\?id=";

    private final WorkshopModsFacade modsFacade;
    private final ModPresetsService modPresetsService;

    @Autowired
    public ArmaLauncherPresetService(WorkshopModsFacade modsFacade, ModPresetsService modPresetsService) {
        this.modsFacade = modsFacade;
        this.modPresetsService = modPresetsService;
    }

    public List<WorkshopMod> importPreset(Document htmlPresetDocument) {
        Elements links = htmlPresetDocument.select(MODS_HREF_CSS_QUERY);
        List<Long> modIds = links.stream()
                .map(link -> link.attr("href").replaceFirst(LINK_DELETE_REGEX, ""))
                .map(this::convertModIdToLong)
                .toList();

        List<WorkshopMod> importedMods = modsFacade.saveAndInstallMods(modIds);

        int modPresetId = 1;
        String modPresetName = "Imported preset " + modPresetId;
        while (modPresetsService.presetWithNameExists(modPresetName)) {
            modPresetId++;
            modPresetName = "Imported preset " + modPresetId;
        }
        ModPreset modPreset = new ModPreset(modPresetName, importedMods, ServerType.ARMA3);
        modPresetsService.savePreset(modPreset);

        return importedMods;
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
