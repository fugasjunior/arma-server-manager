package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class DayZServer extends Server {

    @Min(1)
    private long instanceId;

    @Min(0)
    private int respawnTime;

    private boolean persistent;
    private boolean vonEnabled;
    private boolean forceSameBuild;
    private boolean thirdPersonViewEnabled;
    private boolean crosshairEnabled;
    private boolean clientFilePatching;

    @DecimalMin("0.1")
    @DecimalMax("64")
    private double timeAcceleration;

    @DecimalMin("0.1")
    @DecimalMax("64")
    private double nightTimeAcceleration;

    @Column(columnDefinition = "LONGTEXT")
    private String additionalOptions;

    @ManyToMany
    private List<WorkshopMod> activeMods;
}
