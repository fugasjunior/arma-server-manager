package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.dtos.Scenario;
import cz.forgottenempire.arma3servergui.services.ScenarioService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/scenarios")
public class ScenarioController {

    @Value("${serverDir}")
    private String serverDir;

    private ScenarioService scenarioService;

    @GetMapping
    public ResponseEntity<List<Scenario>> getAllScenarios() {
        return new ResponseEntity<>(scenarioService.getAllScenarios(), HttpStatus.OK);
    }

    @GetMapping("/{name:.+}")
    public ResponseEntity<Resource> downloadScenario(@PathVariable String name) {
        String modPath = serverDir + File.separatorChar + "mpmissions" + File.separatorChar + name;
        File file = new File(modPath);

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=" + file.getName())
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.warn("File {} not found", modPath);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
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

    @DeleteMapping("/{name:.+}")
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
