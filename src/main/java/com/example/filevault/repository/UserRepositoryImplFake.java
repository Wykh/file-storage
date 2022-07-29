package com.example.filevault.repository;

import com.google.common.collect.Lists;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.filevault.config.ApplicationUserRole.ADMIN;
import static com.example.filevault.config.ApplicationUserRole.USER;

@Repository("fake")
public class UserRepositoryImplFake implements UserDao {

    private final PasswordEncoder passwordEncoder;

    public UserRepositoryImplFake(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<User> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    private List<User> getApplicationUsers() {
        return Lists.newArrayList(
                new User(
                        "annasmith",
                        passwordEncoder.encode("password"),
                        USER.getGrantedAuthorities()
                ),
                new User(
                        "linda",
                        passwordEncoder.encode("password"),
                        ADMIN.getGrantedAuthorities()
                ),
                new User(
                        "tom",
                        passwordEncoder.encode("password"),
                        ADMIN.getGrantedAuthorities()
                )
        );
    }
}
