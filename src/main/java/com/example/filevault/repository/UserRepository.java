package com.example.filevault.repository;

import com.example.filevault.entity.UserEntity;

import java.util.Optional;

public interface UserRepository {
    Optional<UserEntity> selectApplicationUserByUsername(String username);
}
