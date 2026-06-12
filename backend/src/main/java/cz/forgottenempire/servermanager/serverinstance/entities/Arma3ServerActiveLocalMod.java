package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.localmod.LocalMod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "arma3server_active_local_mods")
public class Arma3ServerActiveLocalMod implements ActiveModEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "active_local_mods_id")
    private LocalMod mod;

    @Column(name = "position")
    private int position;

    @Override
    public boolean isServerOnly() {
        return mod.isServerOnly();
    }

    @Override
    public boolean isLoadOnHeadlessClient() {
        return mod.isLoadOnHeadlessClient();
    }

    @Override
    public String getLaunchName() {
        return mod.getName();
    }
}
