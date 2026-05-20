package cz.forgottenempire.servermanager.security.user;

import cz.forgottenempire.servermanager.api.UsersApi;
import cz.forgottenempire.servermanager.api.model.*;
import cz.forgottenempire.servermanager.security.UserManagementMapper;
import cz.forgottenempire.servermanager.security.permission.PermissionCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class UserController implements UsersApi {

    private final UserService userService;
    private final UserManagementMapper mapper;

    @Autowired
    public UserController(UserService userService, UserManagementMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_ADMIN + "')")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(userService.getAllUsers().stream()
                .map(mapper::toUserDto)
                .collect(Collectors.toList()));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_ADMIN + "')")
    public ResponseEntity<UserDto> getUser(Long id) {
        return ResponseEntity.ok(mapper.toUserDto(userService.getUser(id)));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_ADMIN + "')")
    public ResponseEntity<UserDto> createUser(CreateUserRequest request) {
        User user = userService.createUser(request.getUsername(), request.getPassword());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toUserDto(user));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_ADMIN + "')")
    public ResponseEntity<UserDto> updateUser(Long id, UpdateUserRequest request) {
        User user = userService.updateUser(id, request.getEnabled());
        return ResponseEntity.ok(mapper.toUserDto(user));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_ADMIN + "')")
    public ResponseEntity<Void> deleteUser(Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_ADMIN + "')")
    public ResponseEntity<UserDto> setUserRoles(Long id, SetUserRolesRequest request) {
        User user = userService.setUserRoles(id, request.getRoleIds());
        return ResponseEntity.ok(mapper.toUserDto(user));
    }

    @Override
    @PreAuthorize("hasAuthority('" + PermissionCode.USER_ADMIN + "')")
    public ResponseEntity<Void> resetUserPassword(Long id, ChangePasswordRequest request) {
        userService.changePassword(id, request.getNewPassword());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CurrentUserDto> getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);
        return ResponseEntity.ok(mapper.toCurrentUserDto(user));
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> changeMyPassword(ChangePasswordRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.changeOwnPassword(username, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
