package cz.forgottenempire.arma3servergui.additionalserver.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
public class AdditionalServer {

    @Id
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String serverDir;
    @NotEmpty
    private String command;
    private String imageUrl;
}
