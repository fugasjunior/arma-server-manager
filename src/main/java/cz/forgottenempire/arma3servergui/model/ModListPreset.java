package cz.forgottenempire.arma3servergui.model;

import java.util.Collection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Exclude;

@Getter
@Setter
@ToString
@Entity
@NoArgsConstructor
public class ModListPreset {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @OneToMany
    @Exclude
    private Collection<WorkshopMod> mods;

    public ModListPreset(
            @NotNull @Size(min = 1, max = 100) String name) {
        this.name = name;
    }
}
