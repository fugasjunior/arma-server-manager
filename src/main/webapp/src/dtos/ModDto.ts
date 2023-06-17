import {ServerType} from "./ServerDto.ts";
import {ErrorStatus, InstallationStatus} from "./Status.ts";

export interface ModDto {
    id: number,
    name: string,
    serverType: ServerType,
    fileSize: number,
    lastUpdated: string,
    installationStatus: InstallationStatus,
    errorStatus: ErrorStatus
}
