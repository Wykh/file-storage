package com.example.fileVault.model;

import com.example.fileVault.entity.FileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class FileModel {
    private UUID id;
    private Date uploadDate;
    private Date modifiedDate;
    private String extension;
    private String name;
    private Long size;
    private String comment;
    private String webUrl;

    public static FileModel toModel(FileEntity entity) {
        return FileModel.builder()
                .id(entity.getId())
                .uploadDate(entity.getUploadDate())
                .modifiedDate(entity.getModifiedDate())
                .extension(entity.getExtension())
                .name(entity.getName())
                .size(entity.getSize())
                .comment(entity.getComment())
                .webUrl("http://localhost:8080/api/file/download/" + entity.getId()).build();
    }

}
