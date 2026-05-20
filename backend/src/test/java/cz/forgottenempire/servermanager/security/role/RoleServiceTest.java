package cz.forgottenempire.servermanager.security.role;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.security.permission.Permission;
import cz.forgottenempire.servermanager.security.permission.PermissionRepository;
import cz.forgottenempire.servermanager.security.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleService roleService;

    @Test
    void getAllRoles() {
        Role role1 = new Role();
        role1.setId(1L);
        role1.setName("Admin");
        Role role2 = new Role();
        role2.setId(2L);
        role2.setName("Viewer");

        when(roleRepository.findAll()).thenReturn(Arrays.asList(role1, role2));

        List<Role> roles = roleService.getAllRoles();

        assertEquals(2, roles.size());
        verify(roleRepository).findAll();
    }

    @Test
    void getRole_found() {
        Role role = new Role();
        role.setId(1L);
        role.setName("Admin");

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Role result = roleService.getRole(1L);

        assertEquals("Admin", result.getName());
        verify(roleRepository).findById(1L);
    }

    @Test
    void getRole_notFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> roleService.getRole(1L));
    }

    @Test
    void createRole_happyPath() {
        Permission perm1 = new Permission("SERVER_VIEW", "View servers");
        Permission perm2 = new Permission("SERVER_OPERATE", "Operate servers");

        when(permissionRepository.findById("SERVER_VIEW")).thenReturn(Optional.of(perm1));
        when(permissionRepository.findById("SERVER_OPERATE")).thenReturn(Optional.of(perm2));
        when(roleRepository.findByName("CustomRole")).thenReturn(Optional.empty());
        when(roleRepository.save(any())).thenAnswer(i -> {
            Role r = i.getArgument(0);
            r.setId(1L);
            return r;
        });

        Role result = roleService.createRole("CustomRole", "A custom role", Arrays.asList("SERVER_VIEW", "SERVER_OPERATE"));

        assertEquals("CustomRole", result.getName());
        assertEquals("A custom role", result.getDescription());
        assertEquals(2, result.getPermissions().size());
        verify(roleRepository).save(any());
    }

    @Test
    void createRole_duplicateName() {
        Role existing = new Role();
        existing.setName("CustomRole");

        when(roleRepository.findByName("CustomRole")).thenReturn(Optional.of(existing));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> roleService.createRole("CustomRole", "Description", List.of()));
        assertEquals("Role name already in use: CustomRole", e.getReason());
    }

    @Test
    void createRole_unknownPermission() {
        when(roleRepository.findByName("CustomRole")).thenReturn(Optional.empty());
        when(permissionRepository.findById("UNKNOWN_PERM")).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> roleService.createRole("CustomRole", "Description", List.of("UNKNOWN_PERM")));
        assertEquals("Unknown permission: UNKNOWN_PERM", e.getReason());
    }

    @Test
    void updateRole_description() {
        Role role = new Role();
        role.setId(1L);
        role.setDescription("Old description");
        Set<Permission> perms = new HashSet<>();
        role.setPermissions(perms);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleRepository.save(any())).thenReturn(role);

        Role result = roleService.updateRole(1L, "New description", null);

        assertEquals("New description", result.getDescription());
        verify(roleRepository).save(role);
    }

    @Test
    void updateRole_permissions() {
        Role role = new Role();
        role.setId(1L);
        role.setPermissions(new HashSet<>());

        Permission perm = new Permission("SERVER_VIEW", "View servers");
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(permissionRepository.findById("SERVER_VIEW")).thenReturn(Optional.of(perm));
        when(roleRepository.save(any())).thenReturn(role);

        roleService.updateRole(1L, null, List.of("SERVER_VIEW"));

        assertEquals(1, role.getPermissions().size());
        verify(roleRepository).save(role);
    }

    @Test
    void deleteRole_happyPath() {
        Role role = new Role();
        role.setId(1L);
        role.setName("CustomRole");
        role.setBuiltIn(false);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.existsByRolesId(1L)).thenReturn(false);

        roleService.deleteRole(1L);

        verify(roleRepository).delete(role);
    }

    @Test
    void deleteRole_builtInProtection() {
        Role role = new Role();
        role.setId(1L);
        role.setBuiltIn(true);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> roleService.deleteRole(1L));
        assertEquals("Built-in role cannot be deleted", e.getReason());
        verify(roleRepository, never()).delete(any());
    }

    @Test
    void deleteRole_assignedToUsers() {
        Role role = new Role();
        role.setId(1L);
        role.setBuiltIn(false);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(userRepository.existsByRolesId(1L)).thenReturn(true);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> roleService.deleteRole(1L));
        assertEquals("Role is assigned to one or more users and cannot be deleted", e.getReason());
        verify(roleRepository, never()).delete(any());
    }
}
