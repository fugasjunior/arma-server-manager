package cz.forgottenempire.servermanager.workshop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SteamAuthDto {

    private String username;
    private String password;
    private String steamGuardToken;
}
