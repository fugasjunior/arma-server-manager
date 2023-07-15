export interface Arma3ScenariosDto {
    scenarios: Array<Arma3ScenarioDto>
}

export interface Arma3ScenarioDto {
    name: string,
    fileSize: number,
    createdOn: Date
}