package cz.forgottenempire.arma3servergui.model;

import javax.persistence.Column;
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
public class CreatorDLC {

    @Id
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String gameId;

    private boolean enabled;

    @Column(columnDefinition = "LONGTEXT")
    private String description;

    private String imageUrl;
}
