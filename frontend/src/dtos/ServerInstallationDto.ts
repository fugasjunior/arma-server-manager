import {ServerType} from "./ServerDto.ts";

export interface ServerInstallationsDto {
    serverInstallations: Array<ServerInstallationDto>
}

export interface ServerInstallationDto {
    type: ServerType,
    version: string,
    installationStatus: string,
    errorStatus: string | null,
    lastUpdatedAt: string,
    branch: string,
    availableBranches: string[]
}
