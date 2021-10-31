package cz.forgottenempire.arma3servergui.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class CreatorDLC {

    @NotNull
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
