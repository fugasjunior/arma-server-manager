<#-- @ftlvariable name="" type="cz.forgottenempire.servermanager.serverinstance.entities.Arma3DifficultySettings" -->
version=1;
blood=1;
singleVoice=0;
gamma=1;
brightness=1;
volumeCD=5;
volumeFX=5;
volumeSpeech=5;
volumeVoN=5;
soundEnableEAX=1;
soundEnableHW=0;
volumeMapDucking=1;
volumeUI=1;
class DifficultyPresets
{
    class CustomDifficulty
    {
        class Options
        {
            /* Simulation */
            reducedDamage = ${reducedDamage?then('1', '0')};        // Reduced damage

            /* Situational awareness */
            groupIndicators = ${groupIndicators};                   // Group indicators (0 = never, 1 = limited distance, 2 = always)
            friendlyTags = ${friendlyTags};                         // Friendly name tags (0 = never, 1 = limited distance, 2 = always)
            enemyTags = ${enemyTags};                               // Enemy name tags (0 = never, 1 = limited distance, 2 = always)
            detectedMines = ${detectedMines};                       // Detected mines (0 = never, 1 = limited distance, 2 = always)
            commands = ${commands};                                 // Commands (0 = never, 1 = fade out, 2 = always)
            waypoints = ${waypoints};                               // Waypoints (0 = never, 1 = fade out, 2 = always)
            tacticalPing = ${tacticalPing};                         // Tactical ping (0 = disabled, 1 = in 3D scene, 2 = on map, 3 = both)

            /* Personal awareness */
            weaponInfo = ${weaponInfo};                             // Weapon info (0 = never, 1 = fade out, 2 = always)
            stanceIndicator = ${stanceIndicator};                   // Stance indicator (0 = never, 1 = fade out, 2 = always)
            staminaBar = ${staminaBar?then('1', '0')};              // Stamina bar
            weaponCrosshair = ${weaponCrosshair?then('1', '0')};    // Weapon crosshair
            visionAid = ${visionAid?then('1', '0')};                // Vision aid

            /* View */
            thirdPersonView = ${thirdPersonView};                   // 3rd person view (0 = disabled, 1 = enabled, 2 = enabled for vehicles only)
            cameraShake = ${cameraShake?then('1', '0')};            // Camera shake

            /* Multiplayer */
            scoreTable = ${scoreTable?then('1', '0')};              // Score table
            deathMessages = ${deathMessages?then('1', '0')};        // Killed by
            vonID = ${vonID?then('1', '0')};                        // VoN ID

            /* Misc */
            mapContent = ${mapContent?then('1', '0')};              // Extended map content
            autoReport = ${autoReport?then('1', '0')};              // Automatic reporting of spotted enemies by players only. This doesn't have any effect on AIs.
            multipleSaves = 0;                                      // Multiple saves
        };

        // aiLevelPreset defines AI skill level and is counted from 0 and can have following values: 0 (Low), 1 (Normal), 2 (High), 3 (Custom).
        // when 3 (Custom) is chosen, values of skill and precision are taken from the class CustomAILevel.
        aiLevelPreset = ${aiLevelPreset};
    };

    class CustomAILevel
    {
        skillAI = ${skillAI};
        precisionAI = ${precisionAI};
    };
};