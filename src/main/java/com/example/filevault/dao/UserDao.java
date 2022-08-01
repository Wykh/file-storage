package com.example.filevault.dao;

import com.example.filevault.config.UserSecurityRole;
import com.example.filevault.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

@Getter
public class UserDao extends User {

    private final UserEntity entity;
    private final UserSecurityRole role;

    public UserDao(UserEntity userEntity, PasswordEncoder passwordEncoder) {
        super(userEntity.getName(),
                passwordEncoder.encode(userEntity.getPassword()),
                UserSecurityRole.valueOf(userEntity.getRole().getName()).getGrantedAuthorities());
        this.entity = userEntity;
        this.role = UserSecurityRole.valueOf(userEntity.getRole().getName());
    }

    public UserDao(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, UserEntity userEntity, PasswordEncoder passwordEncoder) {
        super(userEntity.getName(),
                passwordEncoder.encode(userEntity.getPassword()), enabled, accountNonExpired, credentialsNonExpired, accountNonLocked,
                UserSecurityRole.valueOf(userEntity.getRole().getName()).getGrantedAuthorities());
        this.entity = userEntity;
        this.role = UserSecurityRole.valueOf(userEntity.getRole().getName());
    }


}
