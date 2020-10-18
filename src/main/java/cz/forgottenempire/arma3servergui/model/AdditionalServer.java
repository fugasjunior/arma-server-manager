package cz.forgottenempire.arma3servergui.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class AdditionalServer {

    @NotNull
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
