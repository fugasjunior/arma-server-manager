package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.workshop.WorkshopMod;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "dayzserver_active_mods")
public class DayZServerActiveMod implements ActiveModEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "active_mods_id")
    private WorkshopMod mod;

    @Column(name = "position")
    private int position;

    @Override
    public boolean isLoadOnClient() {
        return mod.isLoadOnClient();
    }

    @Override
    public boolean isLoadOnServer() {
        return mod.isLoadOnServer();
    }

    // Irrelevant for DayZ
    @Override
    public boolean isLoadOnHeadlessClient() {
        return mod.isLoadOnHeadlessClient();
    }

    @Override
    public String getLaunchName() {
        return mod.getNormalizedName();
    }
}
