package com.example.filevault.service;

import com.example.filevault.config.security.UserRole;
import com.example.filevault.dto.UserByEntity;
import com.example.filevault.dto.UserDto;
import com.example.filevault.entity.ChangeRoleHistoryEntity;
import com.example.filevault.entity.RoleEntity;
import com.example.filevault.entity.UserEntity;
import com.example.filevault.exception.BadRoleException;
import com.example.filevault.repository.ChangeRoleHistoryRepository;
import com.example.filevault.repository.RoleRepository;
import com.example.filevault.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.filevault.config.security.UserPermission.BLOCK;
import static com.example.filevault.config.security.UserPermission.CHANGE_ROLE;
import static com.example.filevault.config.security.UserRole.USER;
import static com.example.filevault.util.UserWorkUtils.getCurrentUserName;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ChangeRoleHistoryRepository changeRoleHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserByEntity(
                getOne(username),
                passwordEncoder);
    }

    @Override
    public UserDto registerOne(String username, String password) {
        UserEntity userEntity = UserEntity.builder()
                .name(username)
                .password(password)
                .role(getRoleEntity(USER))
                .isBlocked(false)
                .build();
        userRepository.save(userEntity);
        return UserDto.of(userEntity);
    }

    @Override
    public UserDto updateOne(String username, String newRoleAsString, Boolean isBlocked) {
        UserEntity actorUser = getOne(getCurrentUserName());
        UserEntity targetUser = getOne(username);
        UserRole userRole = UserRole.valueOf(actorUser.getRole().getName());

        List<ChangeRoleHistoryEntity> allByTarget = changeRoleHistoryRepository.findAllByTarget(actorUser);
        List<UserEntity> actorList = allByTarget.stream().map(ChangeRoleHistoryEntity::getActor).toList();

        if (newRoleAsString != null
                && userRole.getPermissions().contains(CHANGE_ROLE)
                && !actorList.contains(targetUser)) {
            UserRole newRole = UserRole.valueOf(newRoleAsString);
            RoleEntity newRoleEntity = getRoleEntity(newRole);
            ChangeRoleHistoryEntity newChangeRoleHistoryEntity =
                    new ChangeRoleHistoryEntity(actorUser, targetUser, newRoleEntity);
            changeRoleHistoryRepository.save(newChangeRoleHistoryEntity);
            targetUser.setRole(newRoleEntity);
        }
        if (isBlocked != null
                && !actorList.contains(targetUser)
                && userRole.getPermissions().contains(BLOCK)) {
            targetUser.setBlocked(isBlocked);
        }
        userRepository.save(targetUser);
        return UserDto.of(targetUser);
    }

    @Override
    public UserEntity getOne(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException(String.format("Username %s not found", username)));
    }

    private RoleEntity getRoleEntity(UserRole roleName) {
        return roleRepository.findByName(roleName.name())
                .orElseThrow(() -> new BadRoleException("Role not found"));
    }
}
