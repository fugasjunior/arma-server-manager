import {
    Arma3DifficultySettingsDto,
    Arma3NetworkSettingsDto,
    BaseServerDto,
    CreatorDlcDto,
    ReforgerModDto,
    ServerWorkshopModDto
} from './generated';

export interface Arma3ServerDto extends BaseServerDto {
    clientFilePatching?: boolean;
    serverFilePatching?: boolean;
    persistent?: boolean;
    battlEye?: boolean;
    vonEnabled?: boolean;
    verifySignatures?: boolean;
    activeMods?: ServerWorkshopModDto[];
    activeDLCs?: CreatorDlcDto[];
    difficultySettings?: Arma3DifficultySettingsDto;
    networkSettings?: Arma3NetworkSettingsDto;
}

export interface DayZServerDto extends BaseServerDto {
    clientFilePatching?: boolean;
    persistent?: boolean;
    verifySignatures?: boolean;
    vonEnabled?: boolean;
    forceSameBuild?: boolean;
    thirdPersonViewEnabled?: boolean;
    crosshairEnabled?: boolean;
    respawnTime?: number;
    timeAcceleration?: number;
    nightTimeAcceleration?: number;
    activeMods?: ServerWorkshopModDto[];
}

export interface ReforgerServerDto extends BaseServerDto {
    scenarioId?: string;
    battlEye?: boolean;
    thirdPersonViewEnabled?: boolean;
    activeMods?: ReforgerModDto[];
}
