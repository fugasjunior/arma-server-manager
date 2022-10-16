package cz.forgottenempire.arma3servergui.additionalserver.controllers;

import cz.forgottenempire.arma3servergui.additionalserver.dtos.AdditionalServerDto;
import cz.forgottenempire.arma3servergui.additionalserver.dtos.AdditionalServersDto;
import cz.forgottenempire.arma3servergui.additionalserver.entities.AdditionalServer;
import cz.forgottenempire.arma3servergui.additionalserver.mappers.AdditionalServerMapper;
import cz.forgottenempire.arma3servergui.additionalserver.services.AdditionalServersService;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/additionalServers")
public class AdditionalServersController {

    private final AdditionalServersService serversService;

    private final AdditionalServerMapper serverMapper = Mappers.getMapper(AdditionalServerMapper.class);

    @Autowired
    public AdditionalServersController(AdditionalServersService serversService) {
        this.serversService = serversService;
    }

    @GetMapping
    public ResponseEntity<AdditionalServersDto> getAdditionalServers() {
        List<AdditionalServerDto> servers = serversService.getAllServers().stream()
                .map(model -> serverMapper.from(model, serversService.getServerInstanceInfo(model.getId())))
                .sorted(Comparator.comparing(AdditionalServerDto::getName))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new AdditionalServersDto(servers));
    }

    @GetMapping("/{serverId}")
    public ResponseEntity<AdditionalServerDto> getAdditionalServer(@PathVariable Long serverId) {
        AdditionalServer server = serversService.getServer(serverId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Additional server ID " + serverId + " not found"));

        return ResponseEntity.ok(serverMapper.from(server, serversService.getServerInstanceInfo(serverId)));
    }

    @PostMapping("/{serverId}/start")
    public ResponseEntity<?> startServer(@PathVariable Long serverId) {
        serversService.startServer(serverId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/{serverId}/stop")
    public ResponseEntity<?> stopServer(@PathVariable Long serverId) {
        serversService.stopServer(serverId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
