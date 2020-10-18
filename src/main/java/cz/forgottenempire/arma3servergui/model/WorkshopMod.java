package cz.forgottenempire.arma3servergui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@Document
public class WorkshopMod {

    @Id
    private Long id;
    private String name;
    private String lastUpdated;
    private boolean installed;
    private boolean failed;
    private boolean active;
    private Long fileSize;

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
