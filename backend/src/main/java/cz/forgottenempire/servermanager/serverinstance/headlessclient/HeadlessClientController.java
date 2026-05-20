package cz.forgottenempire.servermanager.serverinstance.headlessclient;

import cz.forgottenempire.servermanager.api.HeadlessClientApi;
import cz.forgottenempire.servermanager.api.model.SetHeadlessClientsTargetRequest;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAuthority('" + PermissionCode.SERVER_OPERATE + "')")
    public ResponseEntity<Void> setHeadlessClientsTarget(Long id, SetHeadlessClientsTargetRequest request) {
        headlessClientService.setTargetCount(id, request.getTargetHeadlessClientsCount());
        return ResponseEntity.ok().build();
    }
}
