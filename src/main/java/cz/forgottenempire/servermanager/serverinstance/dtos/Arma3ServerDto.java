package cz.forgottenempire.servermanager.serverinstance.dtos;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.workshop.CreatorDlcDto;
import java.util.List;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class Arma3ServerDto implements ServerDto {

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
    private boolean serverFilePatching;
    private boolean persistent;
    private boolean battlEye;
    private boolean vonEnabled;
    private boolean verifySignatures;

    private String additionalOptions;

    private List<ServerWorkshopModDto> activeMods;
    private List<CreatorDlcDto> activeDLCs;
    private Arma3DifficultySettingsDto difficultySettings;
    private Arma3NetworkSettingsDto networkSettings;

    private List<LaunchParameterDto> customLaunchParameters;
}
