package com.example.fileVault.model;

import com.example.fileVault.entity.FileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class FileModelNameAndId {
    private UUID id;
    private String name;

    public static FileModelNameAndId toModel(FileEntity file) {
        return FileModelNameAndId.builder()
                .name(file.getName())
                .id(file.getId())
                .build();
    }
}
