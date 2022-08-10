package com.example.filevault.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FileUpdatableFieldsById {
    private UUID id;
    private String name;
    private String comment;
    private Boolean isPublic;
}
