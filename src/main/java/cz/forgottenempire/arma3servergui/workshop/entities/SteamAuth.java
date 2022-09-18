package cz.forgottenempire.arma3servergui.workshop.entities;

import cz.forgottenempire.arma3servergui.converters.AttributeEncryptor;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SteamAuth {

    @Id
    private Long id;
    private String username;

    @Convert(converter = AttributeEncryptor.class)
    private String password;

    private String steamGuardToken;
}
