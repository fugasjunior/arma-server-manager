package cz.forgottenempire.arma3servergui.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class SteamAuth {

    @Id
    private Long id;
    private String username;
    // TODO add encrypting
    private String password;
    private String steamGuardToken;
}
