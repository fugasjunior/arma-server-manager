package cz.forgottenempire.arma3servergui.model;

import com.bol.secure.Encrypted;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class SteamAuth {

    @Id
    private Long id;
    private String username;
    @Field
    @Encrypted
    private String password;
    private String steamGuardToken;
}
