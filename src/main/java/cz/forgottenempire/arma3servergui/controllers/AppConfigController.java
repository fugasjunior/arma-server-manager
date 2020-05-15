package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.Constants;
import cz.forgottenempire.arma3servergui.model.SteamAuth;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/config")
public class AppConfigController {

    JsonDbService<SteamAuth> steamAuthDb;

    @PostMapping("/auth")
    public ResponseEntity<?> setAuthAccount(@RequestBody SteamAuth auth) {
        log.info("Setting new Steam Auth account: username {}, token {}", auth.getUsername(), auth.getSteamGuardToken());

        SteamAuth prevAuth = steamAuthDb.find(Constants.ACCOUND_DEFAULT_ID, SteamAuth.class);
        if (prevAuth != null && (auth.getPassword() == null || auth.getPassword().isBlank())) {
            // if the user left the password blank, use an existing password if available
            auth.setPassword(prevAuth.getPassword() == null ? "" : prevAuth.getPassword());
        }

        steamAuthDb.save(auth, SteamAuth.class);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/auth")
    public ResponseEntity<SteamAuth> getAuthAccount() {
        SteamAuth steamAuth = steamAuthDb.find(Constants.ACCOUND_DEFAULT_ID, SteamAuth.class);
        if (steamAuth == null) {
            steamAuth = new SteamAuth();
            steamAuth.setId(Constants.ACCOUND_DEFAULT_ID);
        }
        steamAuth.setPassword("");

        return new ResponseEntity<>(steamAuth, HttpStatus.OK);
    }

    @Autowired
    public void setSteamAuthDb(JsonDbService<SteamAuth> steamAuthDb) {
        this.steamAuthDb = steamAuthDb;
    }
}
