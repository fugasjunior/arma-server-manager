package cz.forgottenempire.servermanager.scenario;

import cz.forgottenempire.servermanager.api.ScenariosApi;
import cz.forgottenempire.servermanager.api.model.Arma3ScenarioDto;
import cz.forgottenempire.servermanager.api.model.Arma3ScenariosDto;
import cz.forgottenempire.servermanager.api.model.ReforgerScenarioDto;
import cz.forgottenempire.servermanager.api.model.ReforgerScenariosDto;
import cz.forgottenempire.servermanager.common.Arma3InstancePaths;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.common.exceptions.ServerNotInitializedException;
import cz.forgottenempire.servermanager.installation.ServerInstallationService;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import cz.forgottenempire.servermanager.serverinstance.ServerInstanceService;
import cz.forgottenempire.servermanager.serverinstance.entities.Arma3Server;
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
    private final ServerInstanceService serverInstanceService;
    private final Arma3InstancePaths arma3InstancePaths;

    @Autowired
    public ScenarioController(
            ScenarioService scenarioService,
            ServerInstallationService serverInstallationService,
            ServerInstanceService serverInstanceService,
            Arma3InstancePaths arma3InstancePaths
    ) {
        this.scenarioService = scenarioService;
        this.serverInstallationService = serverInstallationService;
        this.serverInstanceService = serverInstanceService;
        this.arma3InstancePaths = arma3InstancePaths;
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
    @PreAuthorize("hasAuthority('" + PermissionCode.SCENARIO_VIEW + "')")
    public ResponseEntity<Arma3ScenariosDto> getServerScenarios(Long id) {
        requireArma3Server(id);
        return ResponseEntity.ok(new Arma3ScenariosDto().scenarios(scenarioService.getAllScenarios(id)));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SCENARIO_MODIFY + "')")
    public ResponseEntity<Arma3ScenariosDto> uploadServerScenarios(Long id, List<MultipartFile> file) {
        requireArma3Server(id);
        if (file != null) {
            file.forEach(f -> {
                log.info("Receiving file upload ({}) for server {}", f.getOriginalFilename(), id);

                if (f.getOriginalFilename() == null) {
                    log.warn("Could not determine file name, skipping");
                    return;
                }

                if (!f.getOriginalFilename().endsWith(".pbo")) {
                    log.warn("File {} is not a PBO file, skipping", f.getOriginalFilename());
                    return;
                }

                scenarioService.uploadScenarioToServer(id, f);
            });
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Arma3ScenariosDto().scenarios(scenarioService.getAllScenarios(id)));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SCENARIO_VIEW + "')")
    public ResponseEntity<Resource> downloadServerScenario(Long id, String name) {
        requireArma3Server(id);
        File file = arma3InstancePaths.getInstanceMpmissionsPath(id).resolve(name).toFile();
        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=" + file.getName())
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (FileNotFoundException e) {
            log.warn("File {} not found for server {}", file, id);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.SCENARIO_DELETE + "')")
    public ResponseEntity<Void> deleteServerScenario(Long id, String name) {
        requireArma3Server(id);
        log.info("Received request to delete scenario {} for server {}", name, id);
        if (!scenarioService.deleteScenario(id, name)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.noContent().build();
    }

    private void requireArma3Server(long id) {
        serverInstanceService.getServer(id)
                .filter(s -> s instanceof Arma3Server)
                .orElseThrow(() -> new NotFoundException("Arma 3 server with ID " + id + " doesn't exist"));
    }
}
