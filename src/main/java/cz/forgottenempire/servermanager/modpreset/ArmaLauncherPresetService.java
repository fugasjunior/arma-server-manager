package cz.forgottenempire.servermanager.modpreset;

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

    @Autowired
    public ArmaLauncherPresetService(WorkshopModsFacade modsFacade) {
        this.modsFacade = modsFacade;
    }

    public List<WorkshopMod> importPreset(Document htmlPresetDocument) {
        Elements links = htmlPresetDocument.select(MODS_HREF_CSS_QUERY);
        List<Long> modIds = links.stream()
                .map(link -> link.attr("href").replaceFirst(LINK_DELETE_REGEX, ""))
                .map(Long::parseLong)
                .toList();

        return modsFacade.saveAndInstallMods(modIds);
    }
}
