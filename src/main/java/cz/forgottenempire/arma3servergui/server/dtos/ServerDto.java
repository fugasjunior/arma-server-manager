package cz.forgottenempire.arma3servergui.server.dtos;

import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ServerDto {
    private Long id;
    @NotEmpty
    private String name;
    @Min(1)
    private int port;
    @Min(1)
    private int maxPlayers;

    private String password;
    private String adminPassword;

    private boolean clientFilePatching;
    private boolean serverFilePatching;
    private boolean persistent;
    private boolean battlEye;
    private boolean von;
    private boolean verifySignatures;

    private String additionalOptions;

    private List<ServerWorkshopModDto> activeMods;
    private List<ServerCreatorDLCDto> activeDLCs;
}
