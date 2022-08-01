package com.example.filevault.util;

import com.example.filevault.config.UserSecurityRole;
import com.example.filevault.dao.UserDao;
import com.example.filevault.entity.UserEntity;
import com.example.filevault.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.example.filevault.config.UserSecurityRole.*;
import static com.example.filevault.config.UserSecurityRole.USER;

@Slf4j
public class UserWorkUtils {

    public static UserSecurityRole getCurrentUserRole() {
        return ((UserDao) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getRole();
    }

    public static UserEntity getCurrentUserEntity() {
        return ((UserDao) SecurityContextHolder
                .getContext().getAuthentication().getPrincipal()).getEntity();
    }
}
