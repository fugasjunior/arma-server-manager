package cz.forgottenempire.arma3servergui.controllers;

import cz.forgottenempire.arma3servergui.dtos.AdditionalServerDto;
import cz.forgottenempire.arma3servergui.dtos.AdditionalServersDto;
import cz.forgottenempire.arma3servergui.model.AdditionalServer;
import cz.forgottenempire.arma3servergui.services.AdditionalServersService;
import cz.forgottenempire.arma3servergui.services.JsonDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/additionalServers")
public class AdditionalServersController {

    private JsonDbService<AdditionalServer> serverDb;
    private AdditionalServersService serversService;

    @GetMapping
    public ResponseEntity<AdditionalServersDto> getAdditionalServers() {
        List<AdditionalServerDto> servers = serverDb.findAll(AdditionalServer.class).stream()
                .map(model -> AdditionalServerDto.fromModel(model, serversService.isAlive(model.getId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(new AdditionalServersDto(servers));
    }

    @PostMapping("/start/{serverId}")
    public ResponseEntity<?> startServer(@PathVariable Long serverId) {
        serversService.startServer(serverId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/stop/{serverId}")
    public ResponseEntity<?> stopServer(@PathVariable Long serverId) {
        serversService.stopServer(serverId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Autowired
    public void setServerDb(JsonDbService<AdditionalServer> serverDb) {
        this.serverDb = serverDb;
    }

    @Autowired
    public void setServersService(AdditionalServersService serversService) {
        this.serversService = serversService;
    }

}
