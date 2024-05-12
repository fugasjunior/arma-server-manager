package cz.forgottenempire.servermanager.steamcmd.outputprocessor;

public record SteamCmdItemInfo(
        long itemId,
        SteamCmdStatus status,
        long progressPercent,
        long bytesFinished,
        long bytesDone
) {
    public enum SteamCmdStatus {
        FINISHED,
        IN_PROGRESS,
        ERROR
    }
}
