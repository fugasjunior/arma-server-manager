package cz.forgottenempire.servermanager.installation;

import cz.forgottenempire.servermanager.api.ServerInstallationApi;
import cz.forgottenempire.servermanager.api.model.ActiveBranchDto;
import cz.forgottenempire.servermanager.api.model.ServerInstallationDto;
import cz.forgottenempire.servermanager.api.model.ServerInstallationsDto;
import cz.forgottenempire.servermanager.common.ServerType;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('" + PermissionCode.INSTALL_VIEW + "')")
    public ResponseEntity<ServerInstallationsDto> getServerInstallations() {
        List<ServerInstallation> installations = installationService.getAvailableServerInstallations();
        List<ServerInstallationDto> dtos = mapper.map(installations);
        return ResponseEntity.ok(new ServerInstallationsDto().serverInstallations(dtos));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.INSTALL_VIEW + "')")
    public ResponseEntity<ServerInstallationDto> getServerInstallation(ServerType type) {
        ServerInstallation installation = installationService.getServerInstallation(type);
        return ResponseEntity.ok(mapper.map(installation));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.INSTALL_MANAGE + "')")
    public ResponseEntity<ServerInstallationDto> installServer(ServerType type) {
        ServerInstallation serverInstallation = installationService.getServerInstallation(type);
        installerService.installServer(serverInstallation);
        return ResponseEntity.ok(mapper.map(serverInstallation));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.INSTALL_MANAGE + "')")
    public ResponseEntity<Void> setActiveBranch(ServerType type, ActiveBranchDto activeBranchDto) {
        ServerInstallation installation = installationService.getServerInstallation(type);
        ServerInstallation.Branch branch = ServerInstallation.Branch.valueOf(activeBranchDto.getBranch().name());
        installationService.setServerBranch(installation, branch);
        return ResponseEntity.ok().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.INSTALL_MANAGE + "')")
    public ResponseEntity<Void> uninstallServer(ServerType type) {
        ServerInstallation installation = installationService.getServerInstallation(type);
        uninstallerService.uninstallServer(installation);
        return ResponseEntity.noContent().build();
    }
}
