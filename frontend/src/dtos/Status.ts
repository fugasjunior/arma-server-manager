export enum ErrorStatus {
    WRONG_AUTH,
    IO,
    TIMEOUT,
    NO_MATCH,
    NO_SUBSCRIPTION,
    RATE_LIMIT,
    GENERIC,
    INTERRUPTED
}

export enum InstallationStatus {
    INSTALLATION_IN_PROGRESS,
    ERROR,
    FINISHED
}