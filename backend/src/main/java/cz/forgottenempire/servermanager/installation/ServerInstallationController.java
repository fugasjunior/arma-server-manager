package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.api.ServerInstallationApi;
import cz.forgottenempire.servermanager.api.model.ActiveBranchDto;
import cz.forgottenempire.servermanager.api.model.ServerInstallationDto;
import cz.forgottenempire.servermanager.api.model.ServerInstallationsDto;
import cz.forgottenempire.servermanager.common.ServerType;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ServerInstallationController implements ServerInstallationApi {

    private final ServerInstallationService installationService;
    private final ServerInstallerService installerService;
    private final ServerUninstallerService uninstallerService;
    private final ServerInstallationMapper mapper;

    @Autowired
    public ServerInstallationController(
            ServerInstallationService installationService,
            ServerInstallerService installerService,
            ServerUninstallerService uninstallerService,
            ServerInstallationMapper mapper
    ) {
        this.installationService = installationService;
        this.installerService = installerService;
        this.uninstallerService = uninstallerService;
        this.mapper = mapper;
    }

    @Override
    @Cacheable("serverInstallationsResponse")
    public ResponseEntity<ServerInstallationsDto> getServerInstallations() {
        log.debug("Getting server installations");
        List<ServerInstallation> installations = installationService.getAvailableServerInstallations();
        List<ServerInstallationDto> dtos = mapper.map(installations);
        return ResponseEntity.ok(new ServerInstallationsDto().serverInstallations(dtos));
    }

    @Override
    public ResponseEntity<ServerInstallationDto> getServerInstallation(ServerType type) {
        ServerInstallation installation = installationService.getServerInstallation(type);
        return ResponseEntity.ok(mapper.map(installation));
    }

    @Override
    public ResponseEntity<ServerInstallationDto> installServer(ServerType type) {
        ServerInstallation serverInstallation = installationService.getServerInstallation(type);
        installerService.installServer(serverInstallation);
        return ResponseEntity.ok(mapper.map(serverInstallation));
    }

    @Override
    public ResponseEntity<Void> setActiveBranch(ServerType type, ActiveBranchDto activeBranchDto) {
        ServerInstallation installation = installationService.getServerInstallation(type);
        ServerInstallation.Branch branch = ServerInstallation.Branch.valueOf(activeBranchDto.getBranch().name());
        installationService.setServerBranch(installation, branch);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> uninstallServer(ServerType type) {
        ServerInstallation installation = installationService.getServerInstallation(type);
        uninstallerService.uninstallServer(installation);
        return ResponseEntity.noContent().build();
    }
}
