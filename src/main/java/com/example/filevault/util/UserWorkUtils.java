package com.example.filevault.util;

import com.example.filevault.config.security.UserRole;
import com.example.filevault.dao.UserDao;
import com.example.filevault.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class UserWorkUtils {

    public static UserRole getCurrentUserRole() {
        return ((UserDao) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getRole();
    }

    public static UserEntity getCurrentUserEntity() {
        return ((UserDao) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getEntity();
    }
}
