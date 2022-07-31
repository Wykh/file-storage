package com.example.filevault.entity;

import lombok.*;
import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class ChangeRoleHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="actor_user_id", referencedColumnName = "id", nullable = false)
    @NonNull
    private UserEntity actor;
    @ManyToOne
    @JoinColumn(name="target_user_id", referencedColumnName = "id", nullable = false)
    @NonNull
    private UserEntity target;
    @ManyToOne
    @JoinColumn(name="role_id", referencedColumnName = "id", nullable = false)
    @NonNull
    private RoleEntity role;
}
