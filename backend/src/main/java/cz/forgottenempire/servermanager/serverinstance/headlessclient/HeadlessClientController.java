package cz.forgottenempire.servermanager.serverinstance.headlessclient;

import cz.forgottenempire.servermanager.api.HeadlessClientApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class HeadlessClientController implements HeadlessClientApi {

    private final HeadlessClientService headlessClientService;

    @Autowired
    public HeadlessClientController(HeadlessClientService headlessClientService) {
        this.headlessClientService = headlessClientService;
    }

    @Override
    public ResponseEntity<Void> startHeadlessClient(Long id) {
        headlessClientService.addHeadlessClient(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> stopHeadlessClient(Long id) {
        headlessClientService.removeHeadlessClient(id);
        return ResponseEntity.ok().build();
    }
}
