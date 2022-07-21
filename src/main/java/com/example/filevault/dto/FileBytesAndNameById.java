package com.example.filevault.dto;

import com.example.filevault.entity.FileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class FileBytesAndNameById {
    private UUID id;
    private String name;
    private String extension;
    private Long size;
    private byte[] content; // TODO: create content stream instead this

    public static FileBytesAndNameById of(FileEntity entity, byte[] content) {
        return FileBytesAndNameById.builder()
                .id(entity.getId())
                .name(entity.getName())
                .extension(entity.getExtension())
                .size(entity.getSize())
                .content(content)
                .build();
    }
}
