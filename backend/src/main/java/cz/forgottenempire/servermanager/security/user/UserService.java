package cz.forgottenempire.servermanager.security.user;

import cz.forgottenempire.servermanager.common.exceptions.NotFoundException;
import cz.forgottenempire.servermanager.security.permission.Permission;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import cz.forgottenempire.servermanager.security.role.Role;
import cz.forgottenempire.servermanager.security.role.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(NotFoundException::new);
    }

    @Transactional
    public User createUser(String username, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already taken: " + username);
        }
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, Boolean enabled) {
        User user = getUser(id);
        if (enabled != null && !enabled) {
            assertNotLastAdmin(user);
        }
        if (enabled != null) {
            user.setEnabled(enabled);
        }
        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = getUser(id);
        assertNotLastAdmin(user);
        userRepository.delete(user);
    }

    @Transactional
    public User setUserRoles(Long id, List<Long> roleIds) {
        User user = getUser(id);
        Set<Role> roles = new HashSet<>();
        for (Long roleId : roleIds) {
            roles.add(roleRepository.findById(roleId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown role id: " + roleId)));
        }
        boolean hadUserAdmin = user.getPermissionCodes().contains(PermissionCode.USER_ADMIN);
        boolean willHaveUserAdmin = roles.stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(Permission::getCode)
                .anyMatch(PermissionCode.USER_ADMIN::equals);
        if (hadUserAdmin && !willHaveUserAdmin) {
            assertNotLastAdmin(user);
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Transactional
    public void changePassword(Long id, String newPassword) {
        User user = getUser(id);
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void changeOwnPassword(String username, String currentPassword, String newPassword) {
        User user = getUserByUsername(username);
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private void assertNotLastAdmin(User user) {
        boolean userHasUserAdmin = user.getPermissionCodes().contains(PermissionCode.USER_ADMIN);
        if (!userHasUserAdmin) return;

        long adminCount = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(user.getId()))
                .filter(u -> u.getPermissionCodes().contains(PermissionCode.USER_ADMIN))
                .count();
        if (adminCount == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Cannot delete the last user with USER_ADMIN permission");
        }
    }

    private void assertAtLeastOneAdmin() {
        boolean anyAdmin = userRepository.findAll().stream()
                .anyMatch(u -> u.getPermissionCodes().contains(PermissionCode.USER_ADMIN));
        if (!anyAdmin) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Operation would leave no user with USER_ADMIN permission");
        }
    }
}
