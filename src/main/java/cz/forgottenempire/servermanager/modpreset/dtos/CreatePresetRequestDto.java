package cz.forgottenempire.servermanager.modpreset.dtos;

import cz.forgottenempire.servermanager.common.ServerType;
import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreatePresetRequestDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private List<Long> mods;
    @NotNull
    private ServerType type;
}
