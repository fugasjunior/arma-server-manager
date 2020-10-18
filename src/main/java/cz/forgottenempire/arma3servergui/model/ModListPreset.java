package cz.forgottenempire.arma3servergui.model;

import java.util.Collection;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@NoArgsConstructor
public class ModListPreset {

    @Id
    @NotNull
    @Size(min = 1, max = 100)
    private String name;
    @NotEmpty
    private Collection<WorkshopMod> mods;

    public ModListPreset(
            @NotNull @Size(min = 1, max = 100) String name) {
        this.name = name;
    }
}
