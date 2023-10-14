package cz.forgottenempire.servermanager.modpreset;

import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ModPreset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(unique = true)
    private String name;

    @ManyToMany
    @JoinTable(
            name = "preset_mod",
            joinColumns = @JoinColumn(name = "preset_id"),
            inverseJoinColumns = @JoinColumn(name = "mod_id"))
    private List<WorkshopMod> mods;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ServerType type;

    public ModPreset(String name, List<WorkshopMod> mods, ServerType type) {
        this.name = name;
        this.mods = mods;
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModPreset modPreset = (ModPreset) o;
        return name.equals(modPreset.name) && mods.equals(modPreset.mods) && type == modPreset.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mods, type);
    }
}
