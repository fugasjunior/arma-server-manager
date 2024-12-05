export interface ModPresetDto {
    id?: string,
    name: string,
    mods: Array<ModPresetModDto>,
    type: string
}

export interface ModPresetDto {
    id?: string,
    name: string,
    mods: Array<ModPresetModDto>,
    type: string
}

export interface ModPresetRequestDto {
    name: string,
    mods: Array<number>,
}

export interface ModPresetModDto {
    id: number,
    name: string,
    shortName: string
}

export interface ModPresetRenameDto {
    name: string
}