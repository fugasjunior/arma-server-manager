package cz.forgottenempire.arma3servergui.system;

import cz.forgottenempire.arma3servergui.workshop.SteamAuthDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/config")
public class ConfigurationController {

    private SteamAuthService authService;

    @PostMapping("/auth")
    public ResponseEntity<?> setAuthAccount(@RequestBody SteamAuthDto auth) {
        authService.setAuthAccount(auth);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/auth")
    public ResponseEntity<SteamAuthDto> getAuthAccount() {
        SteamAuth steamAuth = authService.getAuthAccount();
        SteamAuthDto dto = new SteamAuthDto();
        dto.setUsername(steamAuth.getUsername());
        dto.setSteamGuardToken(steamAuth.getSteamGuardToken());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Autowired
    public void setAuthService(SteamAuthService authService) {
        this.authService = authService;
    }
}
