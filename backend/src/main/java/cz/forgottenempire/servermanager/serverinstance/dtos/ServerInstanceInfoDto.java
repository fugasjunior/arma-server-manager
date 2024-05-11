package cz.forgottenempire.servermanager.serverinstance.dtos;

public record ServerInstanceInfoDto(
        boolean alive,
        String startedAt,
        int playersOnline,
        int maxPlayers,
        String version,
        String map,
        String description,
        int headlessClientsCount
) {
}
