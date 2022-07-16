package com.example.file.vault.dto;

import com.example.file.vault.entity.FileEntity;
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
