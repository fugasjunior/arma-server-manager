package cz.forgottenempire.arma3servergui.model;

import io.jsondb.annotation.Document;
import io.jsondb.annotation.Id;
import io.jsondb.annotation.Secret;
import lombok.Data;

@Data
@Document(collection = "steamAuth", schemaVersion = "1.0")
public class SteamAuth {
    @Id
    private Long id;
    private String username;
    @Secret
    private String password;
    private String steamGuardToken;
}
