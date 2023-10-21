import {ReforgerModDto} from "./ReforgerModDto.ts";
import {ServerWorkshopModDto} from "./ServerWorkshopModDto.ts";
import {CreatorDlcDto} from "./CreatorDlcDto.ts";

export enum ServerType {
    ARMA3 = 'ARMA3',
    REFORGER = 'REFORGER',
    DAYZ = 'DAYZ',
    DAYZ_EXP = 'DAYZ_EXP'
}

export interface ServerDto {
    type: string,
    id?: number,
    name: string,
    description: string,
    port: number,
    queryPort: number,
    password: string,
    adminPassword: string,
    maxPlayers: number
    instanceInfo?: ServerInstanceInfoDto
    customLaunchParameters: Array<{name: string, value: string | null}>
}

export interface Arma3ServerDto extends ServerDto {
    clientFilePatching: boolean,
    serverFilePatching: boolean,
    persistent: boolean,
    battlEye: boolean,
    vonEnabled: boolean,
    verifySignatures: boolean,
    activeMods: Array<ServerWorkshopModDto>,
    activeDLCs: Array<CreatorDlcDto>,
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
    activeMods: Array<ServerWorkshopModDto>,
    additionalOptions: string
}

export interface ReforgerServerDto extends ServerDto {
    scenarioId: string,
    battlEye: boolean,
    thirdPersonViewEnabled: boolean,
    activeMods: Array<ReforgerModDto>
}