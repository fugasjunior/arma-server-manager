package cz.forgottenempire.arma3servergui.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "mods", schemaVersion = "1.0")
public class WorkshopMod {
    @Id
    private Long id;
    private String name;
    private boolean installed;

    public WorkshopMod(Long id) {
        this.id = id;
    }

    // as the download and install process happens in another thread, better to synchronize this setter
    public void setInstalled(Boolean installed){
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
