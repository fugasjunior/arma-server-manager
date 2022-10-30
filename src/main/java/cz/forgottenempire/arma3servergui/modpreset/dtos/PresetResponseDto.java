package cz.forgottenempire.arma3servergui.modpreset.dtos;

import cz.forgottenempire.arma3servergui.common.ServerType;
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
