package cz.forgottenempire.servermanager.workshop;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cz.forgottenempire.servermanager.common.InstallationStatus;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.steamcmd.ErrorStatus;
import java.time.LocalDateTime;

import jakarta.persistence.*;
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
}
