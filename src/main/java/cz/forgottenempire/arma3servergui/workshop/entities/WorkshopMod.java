package cz.forgottenempire.arma3servergui.workshop.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.forgottenempire.arma3servergui.server.serverinstance.entities.Server;
import cz.forgottenempire.arma3servergui.steamcmd.ErrorStatus;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class WorkshopMod {

    @Id
    private Long id;
    private String name;
    private String lastUpdated;
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    private InstallationStatus installationStatus;

    @Enumerated(EnumType.STRING)
    private ErrorStatus errorStatus;

    @ManyToMany(mappedBy = "activeMods")
    private List<Server> servers;

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

    public enum InstallationStatus {
        INSTALLATION_QUEUED,
        INSTALLATION_IN_PROGRESS,
        ERROR,
        FINISHED
    }
}
