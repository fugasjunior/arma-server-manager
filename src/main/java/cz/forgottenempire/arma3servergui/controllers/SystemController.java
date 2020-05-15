package cz.forgottenempire.arma3servergui.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Value("${installDir}")
    private String installDir;

    @GetMapping("/space")
    public ResponseEntity<Long> getSpaceLeftOnDevice() {
        File file = new File(installDir);
        return new ResponseEntity<>(file.getUsableSpace(), HttpStatus.OK);
    }
}
