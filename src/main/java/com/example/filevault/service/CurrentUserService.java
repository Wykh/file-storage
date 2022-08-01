package com.example.filevault.service;

import com.example.filevault.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class CurrentUserService {
    private UserEntity entity;
}
