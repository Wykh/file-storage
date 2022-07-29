package com.example.filevault.config;

public enum UserSecurityPermission {
    FILE_READ("file:read"),
    FILE_WRITE("file:write"),
    USER_READ("user:read"),
    USER_WRITE("user:write");

    private final String permission;

    UserSecurityPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}