package cz.forgottenempire.servermanager.modpreset.dtos;

import cz.forgottenempire.servermanager.common.ServerType;
import java.util.List;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class PresetResponseDto {

    @Id
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    List<PresetResponseModDto> mods;

    @NotEmpty
    private ServerType type;
}
