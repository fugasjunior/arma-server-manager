package cz.forgottenempire.arma3servergui.workshop;

import lombok.Data;

@Data
public class SteamAuthDto {

    private String username;
    private String password;
    private String steamGuardToken;
}
