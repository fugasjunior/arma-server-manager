package cz.forgottenempire.servermanager.serverinstance.dtos;

import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
public class Arma3DifficultySettingsDto {
    // (0 = never, 1 = limited distance, 2 = always)
    @Min(0)
    @Max(2)
    private byte groupIndicators;

    // (0 = never, 1 = limited distance, 2 = always)
    @Min(0)
    @Max(2)
    private byte friendlyTags;

    // (0 = never, 1 = limited distance, 2 = always)
    @Min(0)
    @Max(2)
    private byte enemyTags;

    // (0 = never, 1 = limited distance, 2 = always)
    @Min(0)
    @Max(2)
    private byte detectedMines;

    // (0 = never, 1 = fade out, 2 = always)
    @Min(0)
    @Max(2)
    private byte commands;

    // (0 = never, 1 = fade out, 2 = always)
    @Min(0)
    @Max(2)
    private byte waypoints;

    // (0 = never, 1 = fade out, 2 = always)
    @Min(0)
    @Max(2)
    private byte weaponInfo;

    // (0 = never, 1 = fade out, 2 = always)
    @Min(0)
    @Max(2)
    private byte stanceIndicator;

    // (0 = disabled, 1 = enabled, 2 = enabled for vehicles only)
    @Min(0)
    @Max(2)
    private byte thirdPersonView;

    private boolean reducedDamage;
    private boolean tacticalPing;
    private boolean staminaBar;
    private boolean weaponCrosshair;
    private boolean visionAid;
    private boolean scoreTable;
    private boolean deathMessages;
    private boolean vonID;
    private boolean mapContent;
    private boolean autoReport;
    private boolean cameraShake;

    // 0 (Low), 1 (Normal), 2 (High), 3 (Custom)
    @Min(0)
    @Max(3)
    private byte aiLevelPreset;

    @Min(0)
    @Max(1)
    private double skillAI;

    @Min(0)
    @Max(1)
    private double precisionAI;
}
