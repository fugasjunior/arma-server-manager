package cz.forgottenempire.arma3servergui.serverinstance.entities;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("4")
public class ReforgerServer extends Server {

    private boolean thirdPersonViewEnabled;
    private boolean battlEye;
}