package cz.forgottenempire.arma3servergui.serverinstance.entities;

import cz.forgottenempire.arma3servergui.common.ServerType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type_intern", discriminatorType = DiscriminatorType.INTEGER)
@DiscriminatorValue("1")
public class Server {

    @Id
    @GeneratedValue
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
