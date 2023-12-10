package cz.forgottenempire.servermanager.serverinstance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/server/{id}/hc")
@Slf4j
class HeadlessClientController {

    private final HeadlessClientService headlessClientService;

    @Autowired
    public HeadlessClientController(HeadlessClientService headlessClientService) {
        this.headlessClientService = headlessClientService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> addHeadlessClient(@PathVariable Long id) {
        headlessClientService.addHeadlessClient(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/stop")
    public ResponseEntity<?> removeHeadlessClient(@PathVariable Long id) {
        headlessClientService.removeHeadlessClient(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
