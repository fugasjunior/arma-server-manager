package cz.forgottenempire.servermanager.security.role;

import cz.forgottenempire.servermanager.api.RolesApi;
import cz.forgottenempire.servermanager.api.model.CreateRoleRequest;
import cz.forgottenempire.servermanager.api.model.RoleDto;
import cz.forgottenempire.servermanager.api.model.UpdateRoleRequest;
import cz.forgottenempire.servermanager.security.UserManagementMapper;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@PreAuthorize("hasAuthority('" + PermissionCode.USER_ADMIN + "')")
public class RoleController implements RolesApi {

    private final RoleService roleService;
    private final UserManagementMapper mapper;

    @Autowired
    public RoleController(RoleService roleService, UserManagementMapper mapper) {
        this.roleService = roleService;
        this.mapper = mapper;
    }

    @Override
    public ResponseEntity<List<RoleDto>> getRoles() {
        return ResponseEntity.ok(roleService.getAllRoles().stream()
                .map(mapper::toRoleDto)
                .collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<RoleDto> getRole(Long id) {
        return ResponseEntity.ok(mapper.toRoleDto(roleService.getRole(id)));
    }

    @Override
    public ResponseEntity<RoleDto> createRole(CreateRoleRequest request) {
        Role role = roleService.createRole(request.getName(), request.getDescription(), request.getPermissions());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toRoleDto(role));
    }

    @Override
    public ResponseEntity<RoleDto> updateRole(Long id, UpdateRoleRequest request) {
        Role role = roleService.updateRole(id, request.getDescription(), request.getPermissions());
        return ResponseEntity.ok(mapper.toRoleDto(role));
    }

    @Override
    public ResponseEntity<Void> deleteRole(Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
