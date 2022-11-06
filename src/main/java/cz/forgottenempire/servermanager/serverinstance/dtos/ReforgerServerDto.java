package cz.forgottenempire.servermanager.serverinstance.dtos;

import cz.forgottenempire.servermanager.common.ServerType;
import java.util.List;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    private String dedicatedServerId;

    @NotEmpty
    private String scenarioId;

    private boolean battlEye;
    private boolean thirdPersonViewEnabled;

    private ServerInstanceInfoDto instanceInfo;

    private List<ReforgerModDto> activeMods;
}
