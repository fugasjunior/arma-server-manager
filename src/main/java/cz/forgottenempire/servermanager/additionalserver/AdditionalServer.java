package cz.forgottenempire.servermanager.additionalserver;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
class AdditionalServer {

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
