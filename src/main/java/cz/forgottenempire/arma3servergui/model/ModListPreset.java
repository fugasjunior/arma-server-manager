package cz.forgottenempire.arma3servergui.model;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
public class ModListPreset {

    @NotNull
    @Id
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @OneToMany
    private Collection<WorkshopMod> mods;

    public ModListPreset(
            @NotNull @Size(min = 1, max = 100) String name) {
        this.name = name;
    }
}
