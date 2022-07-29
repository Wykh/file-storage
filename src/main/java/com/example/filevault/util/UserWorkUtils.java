package com.example.filevault.util;

import com.example.filevault.config.UserSecurityRole;
import lombok.extern.slf4j.Slf4j;

import static com.example.filevault.config.UserSecurityRole.*;
import static com.example.filevault.config.UserSecurityRole.USER;

@Slf4j
public class UserWorkUtils {
    public static UserSecurityRole getUserSecurityRole(String role) {
        UserSecurityRole enumRole;
        switch (role) {
            case "SUPERADMIN" -> enumRole = SUPERADMIN;
            case "ADMIN" -> enumRole = ADMIN;
            case "USER" -> enumRole = USER;
            case "MANAGER" -> enumRole = MANAGER;
            default -> {
                log.info("Role " + role + " not found, using default 'USER' role");
                enumRole = USER;
            }
        }
        return enumRole;
    }
}
