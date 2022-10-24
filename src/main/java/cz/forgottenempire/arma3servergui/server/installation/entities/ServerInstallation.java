package cz.forgottenempire.arma3servergui.server.installation.entities;

import cz.forgottenempire.arma3servergui.server.ServerType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class ServerInstallation {

    @Id
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private ServerType serverType;

}
