package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import cz.forgottenempire.servermanager.workshop.WorkshopModsFacade;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ArmaLauncherPresetServiceTest {

    public static final long ACE_MOD_ID = 463939057L;
    public static final long CBA3_MOD_ID = 450814997L;
    public static final String PRESET_NAME_1 = "Imported preset 1";
    public static final String PRESET_NAME_2 = "Imported preset 2";

    private Document htmlPresetDocument;
    private WorkshopMod aceWorkshopMod;
    private WorkshopMod cba3WorkshopMod;
    private WorkshopModsFacade modsFacade;
    private ModPresetsService modPresetsService;
    private ArmaLauncherPresetService launcherPresetService;

    @BeforeEach
    void setUp() {
        htmlPresetDocument = new Document("/");
        htmlPresetDocument.html(getTestPresetHtml());

        modsFacade = mock(WorkshopModsFacade.class);
        modPresetsService = mock(ModPresetsService.class);

        aceWorkshopMod = new WorkshopMod(ACE_MOD_ID);
        cba3WorkshopMod = new WorkshopMod(CBA3_MOD_ID);

        when(modsFacade.saveAndInstallMods(eq(List.of(ACE_MOD_ID, CBA3_MOD_ID))))
                .thenReturn(List.of(aceWorkshopMod, cba3WorkshopMod));

        launcherPresetService = new ArmaLauncherPresetService(modsFacade, modPresetsService);
    }

    @Test
    void whenImportPresetCalled_thenListOfWorkshopModsReturned() {

        List<WorkshopMod> mods = launcherPresetService.importPreset(htmlPresetDocument);

        verify(modsFacade).saveAndInstallMods(eq(List.of(ACE_MOD_ID, CBA3_MOD_ID)));
        assertThat(mods).hasSize(2);
        assertThat(mods).contains(aceWorkshopMod, cba3WorkshopMod);
    }

    @Test
    void whenImportPresetCalled_thenModPresetCreated() {
        launcherPresetService.importPreset(htmlPresetDocument);

        ModPreset expectedPreset = new ModPreset(PRESET_NAME_1, List.of(aceWorkshopMod, cba3WorkshopMod), ServerType.ARMA3);
        verify(modPresetsService).savePreset(eq(expectedPreset));
    }

    @Test
    void whenImportPresetCalledAndPresetWithSameNameAlreadyExists_thenNewNameIsChosen() {
        when(modPresetsService.presetWithNameExists(PRESET_NAME_1)).thenReturn(true);

        launcherPresetService.importPreset(htmlPresetDocument);

        ModPreset expectedPreset = new ModPreset(PRESET_NAME_2, List.of(aceWorkshopMod, cba3WorkshopMod), ServerType.ARMA3);
        InOrder inOrder = inOrder(modPresetsService);
        inOrder.verify(modPresetsService).presetWithNameExists(PRESET_NAME_1);
        inOrder.verify(modPresetsService).presetWithNameExists(PRESET_NAME_2);
        inOrder.verify(modPresetsService).savePreset(eq(expectedPreset));
    }

    @Test
    void whenModIdCannotBeExtractedFromLink_thenThrowException() {
        Document documentWithInvalidModLink = new Document("/");
        documentWithInvalidModLink.html(
                """
                <html>
                  </head>
                  <body>
                    <div class="mod-list">
                      <a href="http://steamcommunity.com/sharedfiles/filedetails/?id=INVALID_ID" data-type="Link">...</a>
                    </div>
                  </body>
                </html>
                """
        );

        assertThatThrownBy(() -> launcherPresetService.importPreset(documentWithInvalidModLink))
                .isInstanceOf(MalformedLauncherModPresetFileException.class)
                .hasMessage("Invalid workshop mod ID 'INVALID_ID' found in preset HTML file");
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
