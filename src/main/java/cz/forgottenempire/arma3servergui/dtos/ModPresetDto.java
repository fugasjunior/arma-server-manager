package cz.forgottenempire.arma3servergui.dtos;

import java.util.Collection;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class ModPresetDto {
    @Size(min = 1, max = 100)
    private String name;
    @NotEmpty
    private Collection<Long> modIds;
}
