package cz.forgottenempire.arma3servergui.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Document(collection = "additionalServer", schemaVersion = "1.0")
public class AdditionalServer {
    @Id
    @NotNull
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String serverDir;
    @NotEmpty
    private String command;
    private String imageUrl;
}
