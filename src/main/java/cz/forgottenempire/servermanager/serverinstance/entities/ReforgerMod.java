package cz.forgottenempire.servermanager.serverinstance.entities;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class ReforgerMod {

    @NotEmpty
    private String name;
    @NotEmpty
    private String id;
}