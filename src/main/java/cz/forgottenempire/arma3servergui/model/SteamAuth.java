package cz.forgottenempire.arma3servergui.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SteamAuth {

    @Id
    private Long id;
    private String username;
    private String password; // TODO encrypt
    private String steamGuardToken;
}
