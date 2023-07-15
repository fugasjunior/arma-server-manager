interface ServerInstanceInfoDto {
    alive: boolean,
    startedAt: string,
    playersOnline: number,
    maxPlayers: number,
    version: string,
    map: string,
    description: string
}