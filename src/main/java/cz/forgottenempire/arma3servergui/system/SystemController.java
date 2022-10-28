package cz.forgottenempire.arma3servergui.system;

import cz.forgottenempire.arma3servergui.serverinstance.dtos.ServerDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
@Slf4j
class SystemController {

    private final SystemService systemService;

    @Autowired
    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping
    public ResponseEntity<ServerDetails> getServerDetails() {
        ServerDetails details = ServerDetails.builder()
                .spaceLeft(systemService.getDiskSpaceLeft())
                .spaceTotal(systemService.getDiskSpaceTotal())
                .memoryLeft(systemService.getMemoryLeft())
                .memoryTotal(systemService.getMemoryTotal())
                .cpuUsage(systemService.getCpuUsage())
                .cpuCount(systemService.getProcessorCount())
                .osName(systemService.getOsName())
                .osVersion(systemService.getOsVersion())
                .osArchitecture(systemService.getOsArchitecture())
                .build();

        return ResponseEntity.ok(details);
    }
}
