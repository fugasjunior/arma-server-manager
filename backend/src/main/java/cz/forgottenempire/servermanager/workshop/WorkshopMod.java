package cz.forgottenempire.servermanager.workshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class WorkshopMod {

    @Id
    private Long id;
    private String name;
    private LocalDateTime lastUpdated;
    private Long fileSize;

    @Column(name = "server_only")
    private boolean serverOnly;

    @Enumerated(EnumType.STRING)
    private InstallationStatus installationStatus;

    @Enumerated(EnumType.STRING)
    private ErrorStatus errorStatus;

    @Enumerated(EnumType.STRING)
    private ServerType serverType;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "workshop_mod_bikey")
    @Column(name = "bikey")
    private Set<String> biKeys = new HashSet<>();

    public WorkshopMod(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public String getNormalizedName() {
        String retVal = name != null ? name : id.toString();

        retVal = retVal.strip();
        retVal = retVal.replaceAll("[^A-Za-z0-9_]", "");
        retVal = retVal.replaceAll("\\s", "_");
        retVal = "@".concat(retVal);

        return retVal;
    }

    public void addBiKey(String biKey) {
        biKeys.add(biKey);
    }
}
