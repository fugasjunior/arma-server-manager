package cz.forgottenempire.servermanager.serverinstance.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class ModOrderEntry {

    public enum ModSource {
        WORKSHOP, LOCAL
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "mod_source")
    private ModSource source;

    @Column(name = "mod_id")
    private Long modId;
}
