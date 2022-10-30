package cz.forgottenempire.arma3servergui.serverinstance.dtos;

import cz.forgottenempire.arma3servergui.common.ServerType;
import java.util.List;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DayZServerDto implements ServerDto {

    private Long id;

    @NotEmpty
    private String name;

    private String description;

    @Min(1)
    private int port;

    @Min(1)
    private int queryPort;

    @Min(1)
    private int maxPlayers;

    @NotNull(message = "must be filled in. Available types: [ARMA3, DAYZ, DAYZ_EXP, REFORGER]")
    private ServerType type;

    private String password;
    private String adminPassword;

    private boolean clientFilePatching;
    private boolean persistent;
    private boolean verifySignatures;
    private boolean vonEnabled;
    private boolean forceSameBuild;
    private boolean thirdPersonViewEnabled;
    private boolean crosshairEnabled;

    @Min(1)
    private int instanceId;

    @Min(0)
    private int respawnTime;

    @DecimalMin("0.1")
    @DecimalMax("64")
    private double timeAcceleration;

    @DecimalMin("0.1")
    @DecimalMax("64")
    private double nightTimeAcceleration;

    private String additionalOptions;
    private List<ServerWorkshopModDto> activeMods;
    private ServerInstanceInfoDto instanceInfo;
}
