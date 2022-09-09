package cz.forgottenempire.arma3servergui.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Entity
public class CreatorDLC {

    @Id
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String gameId;

    private boolean enabled;
    private String description;
    private String imageUrl;
}
