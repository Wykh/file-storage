package com.example.filevault.service;

import com.example.filevault.config.UserSecurityRole;
import com.example.filevault.entity.UserEntity;
import com.example.filevault.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.example.filevault.config.UserSecurityRole.ADMIN;
import static com.example.filevault.config.UserSecurityRole.USER;

@Service
public class UserServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUserEntity = userRepository.findByName(username);
        UserEntity foundUserEntity = optionalUserEntity.orElseThrow(() ->
                new UsernameNotFoundException(String.format("Username %s not found", username))
        );
        String role = foundUserEntity.getRole().getRole();
        UserSecurityRole enumRole;
        switch (role) {
            case "ADMIN":
                enumRole = ADMIN;
                break;
            case "USER":
                enumRole = USER;
            default:
                enumRole = USER;
        }
        return new User(
                foundUserEntity.getName(),
                passwordEncoder.encode(foundUserEntity.getPassword()),
                enumRole.getGrantedAuthorities()
        );
    }
}
