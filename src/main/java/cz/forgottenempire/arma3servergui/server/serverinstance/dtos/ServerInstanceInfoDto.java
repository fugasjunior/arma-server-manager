package cz.forgottenempire.arma3servergui.server.serverinstance.dtos;

import lombok.Data;

@Data
public class ServerInstanceInfoDto {

    private final boolean alive;
    private final String startedAt;
    private final int playersOnline;
    private final int maxPlayers;
    private final String version;
    private final String map;
    private final String description;
}
