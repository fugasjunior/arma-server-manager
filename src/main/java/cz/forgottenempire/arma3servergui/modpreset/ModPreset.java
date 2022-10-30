package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
}
