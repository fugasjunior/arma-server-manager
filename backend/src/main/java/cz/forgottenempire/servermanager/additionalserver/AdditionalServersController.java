package cz.forgottenempire.servermanager.additionalserver;

import cz.forgottenempire.servermanager.api.AdditionalServersApi;
import cz.forgottenempire.servermanager.api.model.AdditionalServerDto;
import cz.forgottenempire.servermanager.api.model.AdditionalServersDto;
import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdditionalServersController implements AdditionalServersApi {

    private final AdditionalServersService serversService;
    private final AdditionalServerMapper serverMapper;

    @Autowired
    public AdditionalServersController(AdditionalServersService serversService, AdditionalServerMapper serverMapper) {
        this.serversService = serversService;
        this.serverMapper = serverMapper;
    }

    @Override
    public ResponseEntity<AdditionalServersDto> getAdditionalServers() {
        List<AdditionalServerDto> servers = serversService.getAllServers().stream()
                .map(model -> serverMapper.from(model, serversService.getServerInstanceInfo(model.getId())))
                .sorted(Comparator.comparing(AdditionalServerDto::getName))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new AdditionalServersDto().servers(servers));
    }

    @Override
    public ResponseEntity<AdditionalServerDto> getAdditionalServer(Long serverId) {
        AdditionalServer server = serversService.getServer(serverId)
                .orElseThrow(() -> new NotFoundException("Additional server ID " + serverId + " not found"));

        return ResponseEntity.ok(serverMapper.from(server, serversService.getServerInstanceInfo(serverId)));
    }

    @Override
    public ResponseEntity<Void> startAdditionalServer(Long serverId) {
        serversService.startServer(serverId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> stopAdditionalServer(Long serverId) {
        serversService.stopServer(serverId);
        return ResponseEntity.ok().build();
    }
}
