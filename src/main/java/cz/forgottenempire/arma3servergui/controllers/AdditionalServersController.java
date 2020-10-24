package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.dtos.AdditionalServerDto;
import cz.forgottenempire.arma3servergui.dtos.AdditionalServersDto;
import cz.forgottenempire.arma3servergui.model.AdditionalServer;
import cz.forgottenempire.arma3servergui.services.AdditionalServersService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/additionalServers")
public class AdditionalServersController {

    private AdditionalServersService serversService;

    @GetMapping
    public ResponseEntity<AdditionalServersDto> getAdditionalServers() {
        List<AdditionalServerDto> servers = serversService.getAllServers().stream()
                .map(model -> AdditionalServerDto.fromModel(model, serversService.isAlive(model.getId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new AdditionalServersDto(servers));
    }

    @GetMapping("/{serverId}")
    public ResponseEntity<AdditionalServerDto> getAdditionalServer(@PathVariable Long serverId) {
        Optional<AdditionalServer> serverResult = serversService.getServer(serverId);
        if (serverResult.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        AdditionalServer server = serverResult.get();
        boolean alive = serversService.isAlive(serverId);
        return ResponseEntity.ok(AdditionalServerDto.fromModel(server, alive));
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

    @Autowired
    public void setServersService(AdditionalServersService serversService) {
        this.serversService = serversService;
    }

}
