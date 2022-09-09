package cz.forgottenempire.arma3servergui.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
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
