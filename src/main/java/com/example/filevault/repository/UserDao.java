package com.example.filevault.repository;

import org.springframework.security.core.userdetails.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> selectApplicationUserByUsername(String username);
}
