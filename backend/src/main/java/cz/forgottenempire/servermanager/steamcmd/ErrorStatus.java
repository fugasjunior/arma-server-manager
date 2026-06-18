package cz.forgottenempire.servermanager.steamcmd;

public enum ErrorStatus {
    WRONG_AUTH,
    IO,
    TIMEOUT,
    NO_MATCH,
    NO_SUBSCRIPTION,
    RATE_LIMIT,
    GENERIC,
    INTERRUPTED,
    NOT_CONSUMED_BY_GAME,
    SERVER_NOT_INSTALLED,
    REAUTH_REQUIRED
}
