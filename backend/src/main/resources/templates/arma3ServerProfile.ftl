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
            reducedDamage = ${reducedDamage?then('1', '0')};

            /* Situational awareness */
            groupIndicators = ${groupIndicators};
            friendlyTags = ${friendlyTags};
            enemyTags = ${enemyTags};
            detectedMines = ${detectedMines};
            commands = ${commands};
            waypoints = ${waypoints};
            tacticalPing = ${tacticalPing};

            /* Personal awareness */
            weaponInfo = ${weaponInfo};
            stanceIndicator = ${stanceIndicator};
            staminaBar = ${staminaBar?then('1', '0')};
            weaponCrosshair = ${weaponCrosshair?then('1', '0')};
            visionAid = ${visionAid?then('1', '0')};

            /* View */
            thirdPersonView = ${thirdPersonView};
            cameraShake = ${cameraShake?then('1', '0')};

            /* Multiplayer */
            scoreTable = ${scoreTable?then('1', '0')};
            deathMessages = ${deathMessages?then('1', '0')};
            vonID = ${vonID?then('1', '0')};

            /* Misc */
            mapContent = ${mapContent?then('1', '0')};
            autoReport = ${autoReport?then('1', '0')};
            multipleSaves = 0;
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