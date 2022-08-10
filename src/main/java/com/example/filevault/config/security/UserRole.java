package com.example.filevault.config.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static com.example.filevault.config.security.UserPermission.*;

public enum UserRole {
    USER(Set.of(FILE_READ, FILE_WRITE)),
    MANAGER(Set.of(FILE_READ, FILE_WRITE, CHANGE_FILE_ACCESS, BLOCK)),
    ADMIN(Set.of(FILE_READ, FILE_WRITE, FILE_READ_ALL, CHANGE_FILE_ACCESS, BLOCK, CHANGE_ROLE, DELETE_PUBLIC_FILE)),
    SUPERADMIN(Set.of(FILE_READ, FILE_WRITE, USER_READ, USER_WRITE));

    private final Set<UserPermission> permissions;

    UserRole(Set<UserPermission> permissions) {
        this.permissions = permissions;
    }

    public Set<UserPermission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
