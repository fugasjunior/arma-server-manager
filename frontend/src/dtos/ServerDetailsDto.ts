export interface ServerDetailsDto {
    spaceLeft: number,
    spaceTotal: number,
    memoryLeft: number,
    memoryTotal: number,
    cpuUsage: number,
    cpuCount: number,
    osName: string,
    osVersion: string,
    osArchitecture: string
}