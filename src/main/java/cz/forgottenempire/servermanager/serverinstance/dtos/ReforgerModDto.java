package cz.forgottenempire.servermanager.serverinstance.dtos;

import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReforgerModDto {

    @NotEmpty
    private String name;
    @NotEmpty
    private String id;
}
