package com.example.fileVault.model;

import com.example.fileVault.entity.FileEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileModelNameAndId {
    private UUID id;
    private String name;

    public static FileModelNameAndId toModel(FileEntity file) {
        FileModelNameAndId newModel = new FileModelNameAndId();
        newModel.setId(file.getId());
        newModel.setName(file.getName());
        return newModel;
    }
}
