import {ServerType} from "./ServerDto.ts";
import {ErrorStatus, InstallationStatus} from "./Status.ts";

export interface ServerInstallationsDto {
    serverInstallations: Array<ServerInstallationDto>
}

export interface ServerInstallationDto {
    type: ServerType,
    version: string,
    installationStatus: InstallationStatus,
    errorStatus: ErrorStatus,
    lastUpdatedAt: string
}
