package cz.forgottenempire.servermanager.security.role;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.security.permission.Permission;
import cz.forgottenempire.servermanager.security.permission.PermissionRepository;
import cz.forgottenempire.servermanager.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository, PermissionRepository permissionRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    public Role getRole(Long id) {
        return roleRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Role createRole(String name, String description, List<String> permissionCodes) {
        if (roleRepository.findByName(name).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role name already in use: " + name);
        }
        Role role = new Role();
        role.setName(name);
        role.setDescription(description);
        role.setPermissions(resolvePermissions(permissionCodes));
        return roleRepository.save(role);
    }

    @Transactional
    public Role updateRole(Long id, String description, List<String> permissionCodes) {
        Role role = getRole(id);
        if (description != null) {
            role.setDescription(description);
        }
        if (permissionCodes != null) {
            role.setPermissions(resolvePermissions(permissionCodes));
        }
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        Role role = getRole(id);
        if (role.isBuiltIn()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Built-in role cannot be deleted");
        }
        if (userRepository.existsByRolesId(id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Role is assigned to one or more users and cannot be deleted");
        }
        roleRepository.delete(role);
    }

    private Set<Permission> resolvePermissions(List<String> codes) {
        if (codes == null) return new HashSet<>();
        Set<Permission> permissions = new HashSet<>();
        for (String code : codes) {
            permissions.add(permissionRepository.findById(code)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown permission: " + code)));
        }
        return permissions;
    }
}
