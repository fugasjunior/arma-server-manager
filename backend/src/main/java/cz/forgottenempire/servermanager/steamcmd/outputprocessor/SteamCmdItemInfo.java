package cz.forgottenempire.servermanager.steamcmd.outputprocessor;

public record SteamCmdItemInfo(
        long itemId,
        SteamCmdStatus status,
        double progressPercent,
        long bytesFinished,
        long bytesTotal
) {
    public enum SteamCmdStatus {
        FINISHED,
        IN_PROGRESS,
        ERROR
    }
}
