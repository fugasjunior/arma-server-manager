package cz.forgottenempire.arma3servergui.server.serverinstance.entities;

import cz.forgottenempire.arma3servergui.workshop.Arma3CDLC;
import cz.forgottenempire.arma3servergui.workshop.entities.WorkshopMod;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
    @NotNull
    private ServerType type;

    private String description;

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

    @ManyToMany
    @JoinTable(
            name = "server_mod",
            joinColumns = @JoinColumn(name = "server_id"),
            inverseJoinColumns = @JoinColumn(name = "mod_id"))
    private List<WorkshopMod> activeMods;

    @ElementCollection(targetClass = Arma3CDLC.class)
    @Enumerated(EnumType.STRING)
    private List<Arma3CDLC> activeDLCs;

    public enum ServerType {
        ARMA3,
        DAYZ, // TODO currently only Arma 3 is supported
        REFORGER,
        ARMA4 // :)
    }
}
