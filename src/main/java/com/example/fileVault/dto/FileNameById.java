package com.example.fileVault.dto;

import com.example.fileVault.entity.FileEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileNameById {
    private UUID id;
    private String name;

    public static FileNameById toDTO(FileEntity file) {
        FileNameById newModel = new FileNameById();
        newModel.setId(file.getId());
        newModel.setName(file.getName());
        return newModel;
    }
}
