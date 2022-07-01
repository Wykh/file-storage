package com.example.fileVault.dto;

import com.example.fileVault.entity.FileEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class FileNameById {
    private UUID id;
    private String name;

    public static FileNameById toDTO(FileEntity file) {
        return new FileNameById(file.getId(), file.getName());
    }
}
