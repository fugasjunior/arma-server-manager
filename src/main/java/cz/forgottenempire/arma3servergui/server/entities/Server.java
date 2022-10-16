package cz.forgottenempire.arma3servergui.server.entities;

import cz.forgottenempire.arma3servergui.creatorDLC.entities.CreatorDLC;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.List;
import javax.annotation.processing.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Server {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated
    private ServerType type;

    @NotEmpty
    private String name;
    @Min(1)
    private int port;
    @Min(1)
    private int queryPort;
    @Min(1)
    private int maxPlayers;

    private String password;
    private String adminPassword;

    private boolean clientFilePatching;
    private boolean serverFilePatching;

    private boolean persistent;

    private boolean battlEye;
    private boolean von;
    private boolean verifySignatures;

    @Column(columnDefinition = "LONGTEXT")
    private String additionalOptions;

    @OneToMany
    private List<WorkshopMod> activeMods;

    @OneToMany
    private List<CreatorDLC> activeDLCs;

    public enum ServerType {
        ARMA3,
        DAYZ, // TODO currently only Arma 3 is supported
        REFORGER,
        ARMA4 // :)
    }
}
