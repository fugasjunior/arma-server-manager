package cz.forgottenempire.servermanager.system;

import cz.forgottenempire.servermanager.api.SystemApi;
import cz.forgottenempire.servermanager.api.model.OSType;
import cz.forgottenempire.servermanager.api.model.ServerDetailsDto;
import cz.forgottenempire.servermanager.api.model.ServerOSDto;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import cz.forgottenempire.servermanager.util.SystemUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@PreAuthorize("hasAuthority('" + PermissionCode.SYSTEM_VIEW + "')")
public class SystemController implements SystemApi {

    private final SystemService systemService;

    @Autowired
    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @Override
    public ResponseEntity<ServerDetailsDto> getSystemDetails() {
        ServerDetailsDto details = new ServerDetailsDto()
                .spaceLeft(systemService.getDiskSpaceLeft())
                .spaceTotal(systemService.getDiskSpaceTotal())
                .memoryLeft(systemService.getMemoryLeft())
                .memoryTotal(systemService.getMemoryTotal())
                .cpuUsage(systemService.getCpuUsage())
                .cpuCount(systemService.getProcessorCount())
                .osName(systemService.getOsName())
                .osVersion(systemService.getOsVersion())
                .osArchitecture(systemService.getOsArchitecture());
        return ResponseEntity.ok(details);
    }

    @Override
    public ResponseEntity<ServerOSDto> getSystemOs() {
        SystemUtils.OSType localOsType = SystemUtils.getOsType();
        OSType osType = OSType.fromValue(localOsType.name());
        return ResponseEntity.ok(new ServerOSDto().osType(osType));
    }
}
