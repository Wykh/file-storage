package com.example.filevault.service;

import com.example.filevault.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto registerOne(String username, String password);
    UserDto updateOne(String username, String newRole, Boolean isBlocked);
}
