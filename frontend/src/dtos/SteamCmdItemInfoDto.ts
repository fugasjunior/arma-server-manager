export interface SteamCmdItemInfoDto {
    itemId: 233780,
    status: SteamCmdStatus
    progressPercent: 100.0,
    bytesFinished: 0,
    bytesTotal: 0
}

export enum SteamCmdStatus {
    IN_QUEUE = "IN_QUEUE",
    FINISHED = "FINISHED",
    VERIFYING = "VERIFYING",
    DOWNLOADING = "DOWNLOADING",
    PREALLOCATING = "PREALLOCATING",
    COMMITTING = "COMMITTING"
}