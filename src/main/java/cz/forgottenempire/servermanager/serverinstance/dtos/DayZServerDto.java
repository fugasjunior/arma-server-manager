package cz.forgottenempire.servermanager.serverinstance.dtos;

import cz.forgottenempire.servermanager.common.ServerType;
import java.util.List;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    private List<LaunchParameterDto> customLaunchParameters;
}
