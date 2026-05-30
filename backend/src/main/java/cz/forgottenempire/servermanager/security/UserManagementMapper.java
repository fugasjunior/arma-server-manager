package cz.forgottenempire.servermanager.security;

import cz.forgottenempire.servermanager.api.model.CurrentUserDto;
import cz.forgottenempire.servermanager.api.model.PermissionDto;
import cz.forgottenempire.servermanager.api.model.RoleDto;
import cz.forgottenempire.servermanager.api.model.UserDto;
import cz.forgottenempire.servermanager.security.permission.Permission;
import cz.forgottenempire.servermanager.security.role.Role;
import cz.forgottenempire.servermanager.security.user.User;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserManagementMapper {

    public UserDto toUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEnabled(user.isEnabled());
        dto.setCreatedAt(user.getCreatedAt() != null
                ? user.getCreatedAt().atOffset(ZoneOffset.UTC) : null);
        dto.setPasswordChangedAt(user.getPasswordChangedAt() != null
                ? user.getPasswordChangedAt().atOffset(ZoneOffset.UTC) : null);
        dto.setRoles(user.getRoles().stream().map(this::toRoleDto).collect(Collectors.toList()));
        return dto;
    }

    public RoleDto toRoleDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setBuiltIn(role.isBuiltIn());
        dto.setPermissions(role.getPermissions().stream()
                .map(Permission::getCode)
                .collect(Collectors.toList()));
        return dto;
    }

    public PermissionDto toPermissionDto(Permission permission) {
        PermissionDto dto = new PermissionDto();
        dto.setCode(permission.getCode());
        dto.setDescription(permission.getDescription());
        return dto;
    }

    public CurrentUserDto toCurrentUserDto(User user) {
        CurrentUserDto dto = new CurrentUserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setPermissions(List.copyOf(user.getPermissionCodes()));
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toList()));
        return dto;
    }
}
