package cz.forgottenempire.servermanager.serverinstance.entities;

import cz.forgottenempire.servermanager.common.ServerType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Server {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated
    @NotNull
    private ServerType type;

    private String description;

    @NotEmpty
    private String name;
    @Min(1)
    private int port;
    @Min(1)
    private int queryPort;
    @Min(1)
    private int maxPlayers;

    private String password;
    private String adminPassword;
}
