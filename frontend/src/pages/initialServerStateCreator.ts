import {Arma3ServerDto, DayZServerDto, ReforgerServerDto, ServerType} from "../dtos/ServerDto";

export function arma3ServerInitialState(): Arma3ServerDto {
    return {
        type: ServerType.ARMA3,
        name: "",
        description: "",
        port: 2302,
        queryPort: 2303,
        maxPlayers: 32,
        password: "",
        adminPassword: "",
        clientFilePatching: false,
        serverFilePatching: false,
        persistent: true,
        battlEye: true,
        vonEnabled: true,
        verifySignatures: true,
        activeMods: [],
        activeDLCs: [],
        additionalOptions: "headlessClients[]  = {\"127.0.0.1\"}; \nlocalClient[] = { \"127.0.0.1\"};",
        difficultySettings: {
            groupIndicators: 0,
            friendlyTags: 0,
            enemyTags: 0,
            detectedMines: 0,
            commands: 1,
            waypoints: 1,
            weaponInfo: 2,
            stanceIndicator: 2,
            thirdPersonView: 0,
            reducedDamage: false,
            tacticalPing: false,
            staminaBar: false,
            weaponCrosshair: false,
            visionAid: false,
            scoreTable: true,
            deathMessages: true,
            vonID: true,
            mapContent: false,
            autoReport: false,
            cameraShake: true,
            aiLevelPreset: 3,
            skillAI: 0.5,
            precisionAI: 0.5
        },
        customLaunchParameters: [],
        automaticRestart: {
            enabled: false,
            time: null
        }
    };
}

export function dayzServerInitialState(): DayZServerDto {
    return {
        type: ServerType.DAYZ,
        name: "",
        description: "",
        port: 2302,
        queryPort: 27016,
        maxPlayers: 32,
        password: "",
        adminPassword: "",
        clientFilePatching: false,
        persistent: true,
        verifySignatures: false,
        vonEnabled: true,
        forceSameBuild: false,
        thirdPersonViewEnabled: true,
        crosshairEnabled: true,
        respawnTime: 5,
        timeAcceleration: 1.0,
        nightTimeAcceleration: 1.0,
        activeMods: [],
        additionalOptions: `
        class Missions 
        {
            class DayZ
            {
                template = "dayzOffline.chernarusplus";
            };
        };`,
        customLaunchParameters: [],
        automaticRestart: {
            enabled: false,
            time: null
        }
    };
}

export function reforgerServerInitialState(): ReforgerServerDto {
    return {
        type: ServerType.REFORGER,
        name: "",
        description: "",
        scenarioId: "{ECC61978EDCC2B5A}Missions/23_Campaign.conf",
        port: 2001,
        queryPort: 17777,
        maxPlayers: 32,
        password: "",
        adminPassword: "",
        battlEye: true,
        thirdPersonViewEnabled: true,
        activeMods: [],
        customLaunchParameters: [],
        automaticRestart: {
            enabled: false,
            time: null
        }
    };
}