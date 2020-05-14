package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.dtos.Scenario;
import cz.forgottenempire.arma3servergui.services.ScenarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/scenarios")
public class ScenarioController {

    private ScenarioService scenarioService;

    @GetMapping
    public ResponseEntity<List<Scenario>> getAllScenarios() {
        return new ResponseEntity<>(scenarioService.getAllScenarios(), HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadScenario(@RequestParam MultipartFile file) {
        log.info("Receiving file upload ({})", file.getOriginalFilename());

        if (file.getOriginalFilename() == null) {
            return new ResponseEntity<>("Error: Could not determine file name", HttpStatus.BAD_REQUEST);
        }

        if (!file.getOriginalFilename().endsWith(".pbo")) {
            log.warn("File {} is not a PBO file", file.getOriginalFilename());
            return new ResponseEntity<>("Error: PBO file required", HttpStatus.BAD_REQUEST);
        }

        scenarioService.uploadScenarioToServer(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/delete/{name}")
    public ResponseEntity<?> deleteScenario(@PathVariable String name) {
        log.info("Received request to delete scenario {}", name);

        if (!scenarioService.deleteScenario(name)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setScenarioService(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }
}
