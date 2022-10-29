package cz.forgottenempire.arma3servergui.modpreset.dtos;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class UpdatePresetRequestDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private List<Long> mods;
}
