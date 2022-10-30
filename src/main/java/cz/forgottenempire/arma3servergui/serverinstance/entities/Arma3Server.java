package cz.forgottenempire.arma3servergui.serverinstance.entities;

import cz.forgottenempire.arma3servergui.workshop.Arma3CDLC;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import java.util.List;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@DiscriminatorValue("2")
public class Arma3Server extends Server {

    private boolean clientFilePatching;
    private boolean serverFilePatching;

    private boolean persistent;

    private boolean battlEye;
    private boolean vonEnabled;
    private boolean verifySignatures;

    @ManyToMany
    @JoinTable(
            name = "server_mod",
            joinColumns = @JoinColumn(name = "server_id"),
            inverseJoinColumns = @JoinColumn(name = "mod_id"))
    private List<WorkshopMod> activeMods;

    @ElementCollection(targetClass = Arma3CDLC.class)
    @Enumerated(EnumType.STRING)
    private List<Arma3CDLC> activeDLCs;
}
