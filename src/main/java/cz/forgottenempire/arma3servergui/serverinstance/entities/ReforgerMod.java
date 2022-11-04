package cz.forgottenempire.arma3servergui.serverinstance.entities;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotEmpty;
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