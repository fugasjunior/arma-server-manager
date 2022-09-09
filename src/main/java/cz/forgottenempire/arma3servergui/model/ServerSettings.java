package cz.forgottenempire.arma3servergui.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
public class ServerSettings {

    @Id
    private Long id;
    @NotEmpty
    private String name;
    @Min(1)
    private int port;
    @Min(1)
    private int maxPlayers;

    private String password;
    private String adminPassword;

    private boolean clientFilePatching;
    private boolean serverFilePatching;

    private boolean persistent;

    private boolean battlEye;
    private boolean von;
    private boolean verifySignatures;

    private String additionalOptions;

}
