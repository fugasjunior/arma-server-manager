export interface ReforgerScenariosDto {
    scenarios: Array<ReforgerScenarioDto>
}

export interface ReforgerScenarioDto {
    name: string,
    value: string,
    isOfficial: boolean
}