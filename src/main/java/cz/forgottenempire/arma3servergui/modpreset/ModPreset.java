package cz.forgottenempire.arma3servergui.modpreset;

import cz.forgottenempire.arma3servergui.common.ServerType;
import cz.forgottenempire.arma3servergui.workshop.WorkshopMod;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
class ModPreset {

    @Id
    private Long id;

    @NotEmpty
    private String name;

    @ManyToMany
    @JoinTable(
            name = "preset_mod",
            joinColumns = @JoinColumn(name = "preset_id"),
            inverseJoinColumns = @JoinColumn(name = "mod_id"))
    private List<WorkshopMod> mods;

    @Enumerated(EnumType.STRING)
    private ServerType serverType;
}
