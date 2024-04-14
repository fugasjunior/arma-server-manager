package cz.forgottenempire.servermanager.workshop;

import lombok.Data;

@Data
public class SteamAuthDto {

    private String username;
    private String password;
    private String steamGuardToken;
}
