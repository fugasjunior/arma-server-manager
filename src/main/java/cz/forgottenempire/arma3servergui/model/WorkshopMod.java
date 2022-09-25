package cz.forgottenempire.arma3servergui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;
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
    @Deprecated
    private boolean installed; // TODO remove this attribute
    @Deprecated
    private boolean failed; // TODO remove this attribute
    private boolean active;
    private Long fileSize;
    @Embedded
    private DownloadStatus downloadStatus = new DownloadStatus();

    public WorkshopMod(Long id) {
        this.id = id;
    }

    // as the download and install process happens in another thread, better to synchronize this setter
    public void setInstalled(Boolean installed) {
        synchronized (this) {
            this.installed = installed;
        }
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
