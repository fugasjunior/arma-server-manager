package cz.forgottenempire.arma3servergui.controllers;

import com.sun.management.OperatingSystemMXBean;
import cz.forgottenempire.arma3servergui.dtos.ServerDetails;
import cz.forgottenempire.arma3servergui.model.ServerSettings;
import cz.forgottenempire.arma3servergui.services.ArmaServerService;
import cz.forgottenempire.arma3servergui.services.SettingsService;
import java.io.File;
import java.lang.management.ManagementFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @Value("${installDir}")
    private String installDir;

    @Value("${hostName}")
    private String hostName;

    private SettingsService settingsService;
    private ArmaServerService serverService;

    @GetMapping("/space")
    public ResponseEntity<Long> getSpaceLeftOnDevice() {
        File file = new File(installDir);
        return new ResponseEntity<>(file.getUsableSpace(), HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<ServerDetails> getServerDetails() {
        ServerDetails details = new ServerDetails();

        ServerSettings settings = settingsService.getServerSettings();

        details.setUpdating(serverService.isServerUpdating());

        details.setHostName(hostName);
        details.setPort(settings.getPort());

        File file = new File(installDir);
        details.setSpaceLeft(file.getUsableSpace());
        details.setSpaceTotal(file.getTotalSpace());

        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
        details.setMemoryLeft(osBean.getFreePhysicalMemorySize());
        details.setMemoryTotal(osBean.getTotalPhysicalMemorySize());
        details.setCpuUsage(osBean.getSystemCpuLoad());

        return ResponseEntity.ok(details);
    }

    @Autowired
    public void setServerService(ArmaServerService serverService) {
        this.serverService = serverService;
    }

    @Autowired
    public void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }
}
