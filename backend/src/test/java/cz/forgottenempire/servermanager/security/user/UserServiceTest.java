package cz.forgottenempire.servermanager.security.user;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.security.role.Role;
import cz.forgottenempire.servermanager.security.role.RoleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock(lenient = true)
    private UserRepository userRepository;

    @Mock(lenient = true)
    private RoleRepository roleRepository;

    @Mock(lenient = true)
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void getAllUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("alice");
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("bob");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUser_found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUser(1L);

        assertEquals("alice", result.getUsername());
    }

    @Test
    void getUser_notFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
    }

    @Test
    void getUserByUsername_found() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("alice");

        assertEquals("alice", result.getUsername());
    }

    @Test
    void createUser_happyPath() {
        when(userRepository.existsByUsername("alice")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(userRepository.save(any())).thenAnswer(i -> {
            User u = i.getArgument(0);
            u.setId(1L);
            return u;
        });

        User result = userService.createUser("alice", "password123");

        assertEquals("alice", result.getUsername());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any());
    }

    @Test
    void createUser_duplicateUsername() {
        when(userRepository.existsByUsername("alice")).thenReturn(true);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> userService.createUser("alice", "password123"));
        assertEquals("Username already taken: alice", e.getReason());
    }

    @Test
    void updateUser_enable() {
        User user = new User();
        user.setId(1L);
        user.setEnabled(false);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        User result = userService.updateUser(1L, true);

        assertTrue(result.isEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void deleteUser_nonAdminUser() {
        User user = new User();
        user.setId(1L);
        user.setRoles(new HashSet<>()); // No roles, so no USER_ADMIN

        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setRoles(new HashSet<>());
        // Would have USER_ADMIN, but we can't easily mock permissions

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findAll()).thenReturn(Arrays.asList(user, adminUser));

        userService.deleteUser(1L);

        verify(userRepository).delete(user);
    }

    @Test
    void setUserRoles_unknownRoleId() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> userService.setUserRoles(1L, List.of(999L)));
        assertEquals("Unknown role id: 999", e.getReason());
    }

    @Test
    void setUserRoles_unknownRole() {
        User user = new User();
        user.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> userService.setUserRoles(1L, List.of(999L)));
        assertEquals("Unknown role id: 999", e.getReason());
    }

    @Test
    void changePassword() {
        User user = new User();
        user.setId(1L);
        user.setPasswordHash("old_hash");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newpass")).thenReturn("new_hash");
        when(userRepository.save(any())).thenReturn(user);

        userService.changePassword(1L, "newpass");

        assertEquals("new_hash", user.getPasswordHash());
        assertNotNull(user.getPasswordChangedAt());
        verify(userRepository).save(user);
    }

    @Test
    void changeOwnPassword_happyPath() {
        User user = new User();
        user.setId(1L);
        user.setUsername("alice");
        user.setPasswordHash("old_hash");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("old_password", "old_hash")).thenReturn(true);
        when(passwordEncoder.encode("new_password")).thenReturn("new_hash");
        when(userRepository.save(any())).thenReturn(user);

        userService.changeOwnPassword("alice", "old_password", "new_password");

        assertEquals("new_hash", user.getPasswordHash());
        verify(userRepository).save(user);
    }

    @Test
    void changeOwnPassword_wrongCurrentPassword() {
        User user = new User();
        user.setUsername("alice");
        user.setPasswordHash("old_hash");

        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong_password", "old_hash")).thenReturn(false);

        ResponseStatusException e = assertThrows(ResponseStatusException.class,
                () -> userService.changeOwnPassword("alice", "wrong_password", "new_password"));
        assertEquals("Current password is incorrect", e.getReason());
    }
}
