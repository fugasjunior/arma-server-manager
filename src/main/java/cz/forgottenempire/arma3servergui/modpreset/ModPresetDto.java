package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.workshop.ModDto;
import java.util.List;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
class ModPresetDto {

    @Id
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    List<ModDto> mods;
}
