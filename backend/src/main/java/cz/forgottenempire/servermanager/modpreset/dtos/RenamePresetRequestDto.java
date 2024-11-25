package cz.forgottenempire.servermanager.modpreset.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class RenamePresetRequestDto {

    @NotEmpty
    private String name;
}
