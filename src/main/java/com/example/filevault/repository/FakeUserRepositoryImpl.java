package com.example.filevault.repository;

import com.example.filevault.entity.UserEntity;
import com.google.common.collect.Lists;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.filevault.config.ApplicationUserRole.ADMIN;
import static com.example.filevault.config.ApplicationUserRole.USER;

@Repository("fake")
public class FakeUserRepositoryImpl implements UserRepository{

    private final PasswordEncoder passwordEncoder;

    public FakeUserRepositoryImpl(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<UserEntity> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    private List<UserEntity> getApplicationUsers() {
        return Lists.newArrayList(
                new UserEntity(
                        "annasmith",
                        passwordEncoder.encode("password"),
                        USER.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                ),
                new UserEntity(
                        "linda",
                        passwordEncoder.encode("password"),
                        ADMIN.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                ),
                new UserEntity(
                        "tom",
                        passwordEncoder.encode("password"),
                        ADMIN.getGrantedAuthorities(),
                        true,
                        true,
                        true,
                        true
                )
        );
    }
}
