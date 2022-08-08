package com.example.filevault.dto;

import com.example.filevault.config.security.UserRole;
import com.example.filevault.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

@Getter
public class UserByEntity extends User {

    private final UserEntity entity;
    private final UserRole role;

    public UserByEntity(UserEntity userEntity, PasswordEncoder passwordEncoder) {
        super(userEntity.getName(),
                passwordEncoder.encode(userEntity.getPassword()),
                UserRole.valueOf(userEntity.getRole().getName()).getGrantedAuthorities());
        this.entity = userEntity;
        this.role = UserRole.valueOf(userEntity.getRole().getName());
    }

    public UserByEntity(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, UserEntity userEntity, PasswordEncoder passwordEncoder) {
        super(userEntity.getName(),
                passwordEncoder.encode(userEntity.getPassword()),
                enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                UserRole.valueOf(userEntity.getRole().getName()).getGrantedAuthorities());
        this.entity = userEntity;
        this.role = UserRole.valueOf(userEntity.getRole().getName());
    }


}
