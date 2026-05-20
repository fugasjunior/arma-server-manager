package cz.forgottenempire.servermanager.scenario;

import cz.forgottenempire.servermanager.api.ScenariosApi;
import cz.forgottenempire.servermanager.api.model.Arma3ScenarioDto;
import cz.forgottenempire.servermanager.api.model.Arma3ScenariosDto;
import cz.forgottenempire.servermanager.api.model.ReforgerScenarioDto;
import cz.forgottenempire.servermanager.api.model.ReforgerScenariosDto;
import cz.forgottenempire.servermanager.common.PathsFactory;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.ServerNotInitializedException;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
public class ScenarioController implements ScenariosApi {

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

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SCENARIO_VIEW + "')")
    public ResponseEntity<Arma3ScenariosDto> getArma3Scenarios() {
        List<Arma3ScenarioDto> scenarios = scenarioService.getAllScenarios();
        return ResponseEntity.ok(new Arma3ScenariosDto().scenarios(scenarios));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SCENARIO_VIEW + "')")
    public ResponseEntity<Resource> downloadScenario(String name) {
        File file = pathsFactory.getScenarioPath(name).toFile();
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + file.getName())
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.warn("File {} not found", file);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SCENARIO_VIEW + "')")
    public ResponseEntity<ReforgerScenariosDto> getReforgerScenarios() {
        if (!serverInstallationService.isServerInstalled(ServerType.REFORGER)) {
            throw new ServerNotInitializedException(ServerType.REFORGER);
        }
        List<ReforgerScenarioDto> scenarios = scenarioService.getReforgerScenarios();
        return ResponseEntity.ok(new ReforgerScenariosDto().scenarios(scenarios));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SCENARIO_MODIFY + "')")
    public ResponseEntity<Void> uploadScenarios(List<MultipartFile> file) {
        if (file != null) {
            file.forEach(f -> {
                log.info("Receiving file upload ({})", f.getOriginalFilename());

                if (f.getOriginalFilename() == null) {
                    log.warn("Could not determine file name, skipping");
                    return;
                }

                if (!f.getOriginalFilename().endsWith(".pbo")) {
                    log.warn("File {} is not a PBO file, skipping", f.getOriginalFilename());
                    return;
                }

                scenarioService.uploadScenarioToServer(f);
            });
        }
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SCENARIO_DELETE + "')")
    public ResponseEntity<Void> deleteScenario(String name) {
        log.info("Received request to delete scenario {}", name);
        if (!scenarioService.deleteScenario(name)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.noContent().build();
    }
}
