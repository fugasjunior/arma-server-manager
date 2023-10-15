package cz.forgottenempire.servermanager.serverinstance.entities;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity(name = "launch_parameter")
public class LaunchParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Nonnull
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "server_id", nullable = false)
    private Server server;

    @Nonnull
    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;
}
