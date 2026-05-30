package cz.forgottenempire.servermanager.security.permission;

import cz.forgottenempire.servermanager.api.PermissionsApi;
import cz.forgottenempire.servermanager.api.model.PermissionDto;
import cz.forgottenempire.servermanager.security.UserManagementMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class PermissionController implements PermissionsApi {

    private final PermissionRepository permissionRepository;
    private final UserManagementMapper mapper;

    @Autowired
    public PermissionController(PermissionRepository permissionRepository, UserManagementMapper mapper) {
        this.permissionRepository = permissionRepository;
        this.mapper = mapper;
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_ADMIN + "')")
    public ResponseEntity<List<PermissionDto>> getPermissions() {
        List<PermissionDto> dtos = permissionRepository.findAll().stream()
                .map(mapper::toPermissionDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}
