export enum OSType {
    WINDOWS = 'Windows',
    LINUX = 'Linux',
    UNKNOWN = 'Unknown'
}

export interface ServerOSDto {
    osType: string
}