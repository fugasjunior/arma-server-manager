package cz.forgottenempire.servermanager.scenario;

import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.ServerNotInitializedException;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping("/api/scenarios")
class ScenarioController {

    private final ScenarioService scenarioService;
    private final ServerInstallationService serverInstallationService;
    private final PathsFactory pathsFactory;

    @Autowired
    public ScenarioController(
            ScenarioService scenarioService,
            ServerInstallationService serverInstallationService,
            PathsFactory pathsFactory
    ) {
        this.scenarioService = scenarioService;
        this.serverInstallationService = serverInstallationService;
        this.pathsFactory = pathsFactory;
    }

    @GetMapping
    public ResponseEntity<Arma3ScenariosDto> getAllScenarios() {
        return ResponseEntity.ok(new Arma3ScenariosDto(scenarioService.getAllScenarios()));
    }

    @GetMapping("/{name:.+}")
    public ResponseEntity<Resource> downloadScenario(@PathVariable String name) {
        File file = pathsFactory.getScenarioPath(name).toFile();

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=" + file.getName())
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.warn("File {} not found", file);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/REFORGER")
    public ResponseEntity<ReforgerScenariosDto> getReforgerScenarios() {
        if (!serverInstallationService.isServerInstalled(ServerType.REFORGER)) {
            throw new ServerNotInitializedException(ServerType.REFORGER);
        }

        List<ReforgerScenarioDto> reforgerScenarios = scenarioService.getReforgerScenarios();
        return ResponseEntity.ok(new ReforgerScenariosDto(reforgerScenarios));
    }

    @PostMapping
    public ResponseEntity<?> uploadScenarios(@RequestParam("file") List<MultipartFile> files) {
        files.forEach(file -> {
            log.info("Receiving file upload ({})", file.getOriginalFilename());

            if (file.getOriginalFilename() == null) {
                log.warn("Could not determine file name, skipping");
                return;
            }

            if (!file.getOriginalFilename().endsWith(".pbo")) {
                log.warn("File {} is not a PBO file, skipping", file.getOriginalFilename());
                return;
            }

            scenarioService.uploadScenarioToServer(file);
        });

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{name:.+}")
    public ResponseEntity<?> deleteScenario(@PathVariable String name) {
        log.info("Received request to delete scenario {}", name);

        if (!scenarioService.deleteScenario(name)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.noContent().build();
    }
}
