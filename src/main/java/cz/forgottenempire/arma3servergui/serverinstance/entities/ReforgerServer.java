package cz.forgottenempire.arma3servergui.serverinstance.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("4")
public class ReforgerServer extends Server {

    private String dedicatedServerId;

    @NotEmpty
    private String scenarioId;

    private boolean thirdPersonViewEnabled;
    private boolean battlEye;
}