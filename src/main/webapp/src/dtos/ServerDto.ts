export enum ServerType {
    ARMA3 = 'ARMA3',
    REFORGER = 'REFORGER',
    DAYZ = 'DAYZ',
    DAYZ_EXP = 'DAYZ_EXP'
}

export interface ServerDto {
    type: ServerType,
    id?: number,
    name: string,
    description: string,
    port: number,
    queryPort: number,
    password: string,
    adminPassword: string,
    maxPlayers: number
    instanceInfo?: ServerInstanceInfo
}

export interface ServerInstanceInfo {
    alive: boolean
}

export interface Arma3ServerDto extends ServerDto {
    clientFilePatching: boolean,
    serverFilePatching: boolean,
    persistent: boolean,
    battlEye: boolean,
    vonEnabled: boolean,
    verifySignatures: boolean,
    activeMods: any[], // TODO
    activeDLCs: any[], // TODO
    additionalOptions: string,
    difficultySettings: Arma3DifficultySettings
}

export interface Arma3DifficultySettings {
    groupIndicators: 0 | 1 | 2,
    friendlyTags: 0 | 1 | 2,
    enemyTags: 0 | 1 | 2,
    detectedMines: 0 | 1 | 2,
    commands: 0 | 1 | 2,
    waypoints: 0 | 1 | 2,
    weaponInfo: 0 | 1 | 2,
    stanceIndicator: 0 | 1 | 2,
    thirdPersonView: 0 | 1 | 2,
    reducedDamage: boolean,
    tacticalPing: boolean,
    staminaBar: boolean,
    weaponCrosshair: boolean,
    visionAid: boolean,
    scoreTable: boolean,
    deathMessages: boolean,
    vonID: boolean,
    mapContent: boolean,
    autoReport: boolean,
    cameraShake: boolean,
    aiLevelPreset: number,
    skillAI: number,
    precisionAI: number
}

export interface DayZServerDto extends ServerDto {
    clientFilePatching: boolean,
    persistent: boolean,
    verifySignatures: boolean,
    vonEnabled: boolean,
    forceSameBuild: boolean,
    thirdPersonViewEnabled: boolean,
    crosshairEnabled: boolean,
    instanceId: number,
    respawnTime: number,
    timeAcceleration: number,
    nightTimeAcceleration: number,
    activeMods: any[], // TODO
    additionalOptions: string
}

export interface ReforgerServerDto extends ServerDto {
    dedicatedServerId: string,
    scenarioId: string,
    battlEye: boolean,
    thirdPersonViewEnabled: boolean,
    activeMods: any[] // TODO
}