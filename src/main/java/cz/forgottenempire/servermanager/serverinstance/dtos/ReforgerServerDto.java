package cz.forgottenempire.servermanager.serverinstance.dtos;

import cz.forgottenempire.servermanager.common.ServerType;
import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReforgerServerDto implements ServerDto {

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

    @NotEmpty
    private String scenarioId;

    private boolean battlEye;
    private boolean thirdPersonViewEnabled;

    private List<ReforgerModDto> activeMods;

    private List<LaunchParameterDto> customLaunchParameters;

    private AutomaticRestartDto automaticRestart;
}
