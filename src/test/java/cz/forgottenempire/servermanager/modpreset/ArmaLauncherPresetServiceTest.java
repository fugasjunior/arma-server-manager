package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsFacade;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ArmaLauncherPresetServiceTest {

    public static final long ACE_MOD_ID = 463939057L;
    public static final long CBA3_MOD_ID = 450814997L;

    @Test
    void whenImportPresetCalled_thenListOfWorkshopModsReturned() {
        WorkshopModsFacade modsFacade = mock(WorkshopModsFacade.class);
        WorkshopMod aceWorkshopMod = new WorkshopMod(ACE_MOD_ID);
        WorkshopMod cba3WorkshopMod = new WorkshopMod(CBA3_MOD_ID);
        when(modsFacade.saveAndInstallMods(eq(List.of(ACE_MOD_ID, CBA3_MOD_ID))))
                .thenReturn(List.of(aceWorkshopMod, cba3WorkshopMod));
        ArmaLauncherPresetService launcherPresetService = new ArmaLauncherPresetService(modsFacade);
        Document htmlPresetDocument = new Document("/");
        htmlPresetDocument.html(getTestPresetHtml());

        List<WorkshopMod> mods = launcherPresetService.importPreset(htmlPresetDocument);

        verify(modsFacade).saveAndInstallMods(eq(List.of(ACE_MOD_ID, CBA3_MOD_ID)));
        assertThat(mods).hasSize(2);
        assertThat(mods).contains(aceWorkshopMod, cba3WorkshopMod);
    }

    private String getTestPresetHtml() {
        return """
                <?xml version="1.0" encoding="utf-8"?>
                <html>
                  <!--Created by Arma 3 Launcher: https://arma3.com-->
                  <head>
                    <meta name="arma:Type" content="list" />
                    <meta name="generator" content="Arma 3 Launcher - https://arma3.com" />
                    <title>Arma 3</title>
                    <link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet" type="text/css" />
                    <style>
                        body {
                            margin: 0;
                            padding: 0;
                            color: #fff;
                            background: #000;
                        }
                            
                </style>
                  </head>
                  <body>
                    <h1>Arma 3 Mods</h1>
                    <p class="before-list">
                      <em>To import this preset, drag this file onto the Launcher window. Or click the MODS tab, then PRESET in the top right, then IMPORT at the bottom, and finally select this file.</em>
                    </p>
                    <div class="mod-list">
                      <table>
                        <tr data-type="ModContainer">
                          <td data-type="DisplayName">ace</td>
                          <td>
                            <span class="from-steam">Steam</span>
                          </td>
                          <td>
                            <a href="http://steamcommunity.com/sharedfiles/filedetails/?id=463939057" data-type="Link">http://steamcommunity.com/sharedfiles/filedetails/?id=463939057</a>
                          </td>
                        </tr>
                        <tr data-type="ModContainer">
                          <td data-type="DisplayName">CBA_A3</td>
                          <td>
                            <span class="from-steam">Steam</span>
                          </td>
                          <td>
                            <a href="http://steamcommunity.com/sharedfiles/filedetails/?id=450814997" data-type="Link">http://steamcommunity.com/sharedfiles/filedetails/?id=450814997</a>
                          </td>
                        </tr>
                      </table>
                    </div>
                    <div class="footer">
                      <span>Created by Arma 3 Launcher by Bohemia Interactive.</span>
                    </div>
                  </body>
                </html>
                """;
    }
}
