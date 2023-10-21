package cz.forgottenempire.servermanager.serverinstance.dtos;

import jakarta.annotation.Nonnull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LaunchParameterDto {

    private @Nonnull String name;
    private String value;
}
