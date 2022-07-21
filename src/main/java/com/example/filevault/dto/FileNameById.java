package com.example.filevault.dto;

import com.example.filevault.entity.FileEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class FileNameById {
    private UUID id;
    private String name;

    public static FileNameById toDTO(FileEntity file) {
        return new FileNameById(file.getId(), file.getName());
    }
}
