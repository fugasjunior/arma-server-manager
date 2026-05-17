export const BOOLEAN_FIELDS = [
    {name: 'difficultySettings.reducedDamage', label: 'Reduced damage'},
    {name: 'difficultySettings.staminaBar', label: 'Stamina bar'},
    {name: 'difficultySettings.weaponCrosshair', label: 'Weapon crosshair'},
    {name: 'difficultySettings.visionAid', label: 'Vision aid'},
    {name: 'difficultySettings.scoreTable', label: 'Score table'},
    {name: 'difficultySettings.deathMessages', label: 'Killed by'},
    {name: 'difficultySettings.vonID', label: 'VON ID'},
    {name: 'difficultySettings.mapContent', label: 'Extended map content'},
    {name: 'difficultySettings.autoReport', label: 'Auto report'},
    {name: 'difficultySettings.cameraShake', label: 'Camera shake'}
];

export const THREE_STATE_FLAG_FIELDS = [
    {
        name: 'difficultySettings.groupIndicators',
        label: 'Group indicators',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        name: 'difficultySettings.friendlyTags',
        label: 'Friendly tags',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        name: 'difficultySettings.enemyTags',
        label: 'Enemy tags',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        name: 'difficultySettings.detectedMines',
        label: 'Detected mines',
        onLabel: 'Always',
        middleLabel: 'Limited distance',
        offLabel: 'Never'
    },
    {
        name: 'difficultySettings.commands',
        label: 'Commands',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        name: 'difficultySettings.waypoints',
        label: 'Waypoints',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        name: 'difficultySettings.weaponInfo',
        label: 'Weapon info',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        name: 'difficultySettings.stanceIndicator',
        label: 'Stance indicator',
        onLabel: 'Always',
        middleLabel: 'Fade out',
        offLabel: 'Never'
    },
    {
        name: 'difficultySettings.thirdPersonView',
        label: 'Third person view',
        onLabel: 'Vehicles only',
        middleLabel: 'Enabled',
        offLabel: 'Disabled'
    }
];

export const FOUR_STATE_FLAG_FIELDS = [
    {
        name: 'difficultySettings.tacticalPing',
        label: 'Tactical ping',
        zeroLabel: 'Disabled',
        oneLabel: 'In 3D scene',
        twoLabel: 'On map',
        threeLabel: 'Both',
    },
];
