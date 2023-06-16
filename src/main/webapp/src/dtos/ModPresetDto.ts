import {ServerType} from "./ServerDto";

export interface ModPresetDto {
    id: number,
    name: string,
    mods: Array<ModPresetModDto>,
    type: ServerType
}

export interface ModPresetModDto {
    id: number,
    name: string,
    shortName: string
}